package com.example.happylife.backendhappylife.service.implement;

import com.example.happylife.backendhappylife.DTO.InvoiceDTO.InvoiceCreateDTO;
import com.example.happylife.backendhappylife.DTO.PlanDTO.PlanResDTO;
import com.example.happylife.backendhappylife.DTO.RegistrationDTO.RegisResDTO;
import com.example.happylife.backendhappylife.DTO.RegistrationDTO.RegisUpdateDTO;
import com.example.happylife.backendhappylife.DTO.RegistrationDTO.RegisUpdateStatusDTO;
import com.example.happylife.backendhappylife.DTO.UserDTO.UserResDTO;
import com.example.happylife.backendhappylife.entity.Enum.DateUnit;
import com.example.happylife.backendhappylife.entity.Invoice;
import com.example.happylife.backendhappylife.entity.Object.Message;
import com.example.happylife.backendhappylife.entity.Registration;
import com.example.happylife.backendhappylife.entity.Enum.Role;
import com.example.happylife.backendhappylife.repo.RegistrationRepo;
import com.example.happylife.backendhappylife.service.InvoiceService;
import com.example.happylife.backendhappylife.service.RegistrationService;
import jakarta.persistence.EntityNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

import com.example.happylife.backendhappylife.exception.UserCreationException;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationImpl implements RegistrationService {
    @Autowired
    private RegistrationRepo registrationRepo;

    @Autowired
    private InvoiceService invoiceService;




    @Override
    public List<Registration> getRegistrations(UserResDTO user) {
        try {
            if (user.getRole()== Role.INSUARANCE_MANAGER|| user.getRole() == Role.ACCOUNTANT ){
                List<Registration> registrations = registrationRepo.findAll();
                return registrations;
            }
            else if ((user.getRole()== Role.CUSTOMER)) {
                //List<Registration> registrationsUser = new ArrayList<Registration>();
                List<Registration> registrationsUser = registrationRepo.findByCustomerInfo_Id(user.getId());

                /*ObjectId userIdObject = user.getId() != null ? new ObjectId(user.getId()) : null;
                for (Registration regis : registrations) {
                    registrationRepo.findById(userIdObject).ifPresent(registrationsUser::add);
                }*/
                return registrationsUser;
            }
            else {
                throw new UserCreationException("You need authenticated account to access this infomation.");
            }
        }
        catch (Exception e) {
            throw new UserCreationException("Error getting registrations: " + e.getMessage());
        }
    }

    @Override
    public Registration addRegistration(UserResDTO authUser, Registration regis) {
        if (regis.getCustomerInfo().getId() == null || regis.getCustomerInfo().getId().isEmpty()){
            throw new UserCreationException("User ID is required.");
        }
        if (regis.getProductInfo().getPlanId()==null){
            throw new UserCreationException("Plan ID is required.");
        }
        try {

            Instant instantNow= Instant.now();
            regis.setApprovalStatus("Pending");
            regis.setCreatedAt(instantNow);
            regis.setUpdatedAt(instantNow);

            Instant startDate = instantNow.plus(Duration.ofDays(30));
            Instant endDate = startDate ;
            if (regis.getProductInfo().getPlanDurationUnit().equals(DateUnit.Day)){
                endDate = startDate.plus(Duration.ofDays(regis.getProductInfo().getPlanDuration()));
            }
            if (regis.getProductInfo().getPlanDurationUnit().equals(DateUnit.Month)){
                long months = regis.getProductInfo().getPlanDuration();
                endDate = startDate.atZone(ZoneId.systemDefault()).plusMonths(months).toInstant();
            }
            if (regis.getProductInfo().getPlanDurationUnit().equals(DateUnit.Year)){
                long years= regis.getProductInfo().getPlanDuration();
                endDate = startDate.atZone(ZoneId.systemDefault()).plusYears(years).toInstant();
            }
            regis.setStartDate(startDate);
            regis.setEndDate(endDate);
            return registrationRepo.save(regis);

        }
        catch (Exception e) {
            throw new UserCreationException("Error creating registration: " + e.getMessage());
        }
    }
    @Override
    public Registration updateRegisStatus(UserResDTO authUser, ObjectId regisId, RegisUpdateStatusDTO regisUpdateStatusDTO) {
        try {
            RegisResDTO regis = regisUpdateStatusDTO.getRegis();
            Message msg = regisUpdateStatusDTO.getMessage();
            Instant instantNow = Instant.now();
            if (authUser.getRole() == Role.INSUARANCE_MANAGER || authUser.getRole() == Role.ACCOUNTANT ) {
                if (regis.getApprovalStatus().equals("Approved") || regis.getApprovalStatus().equals("Rejected") ||
                        regis.getApprovalStatus().equals("Expired") || regis.getApprovalStatus().equals("Revoked") ||
                        regis.getApprovalStatus().equals("Pending")){
                    Registration regisVar = registrationRepo.findById(regisId)
                            .orElseThrow(() -> new EntityNotFoundException("Regis not found with id: " + regisId));
                    regisVar.setApprovalStatus(regis.getApprovalStatus());
                    msg.setDateMessage(instantNow);
                    if(regisVar.getMessage()!=null){
                        List<Message> msgList = regisVar.getMessage();
                        msgList.add(msg);
                        regisVar.setMessage(msgList);
                    } else{

                        regisVar.setMessage(Arrays.asList(msg));
                    }
                    if (regis.getApprovalStatus().equals("Approved")) {
                        // Tạo InvoiceCreateDTO và gọi phương thức tạo hóa đơn
//                        InvoiceCreateDTO invoiceCreateDTO = new InvoiceCreateDTO();
//                        invoiceCreateDTO.setRegisInfo(regisVar.convertToRegisResDTO());
//
//                        Instant dueDateInstant = regisVar.getEndDate().plus(10, ChronoUnit.DAYS);
//                        invoiceCreateDTO.setDueDate(dueDateInstant);
//                        invoiceCreateDTO.setPaymentStatus("Pending");
//                        Invoice invoice = new Invoice();
//                        Invoice invoiceCreated = invoice.convertCreToInvoice(invoiceCreateDTO);
//                        invoiceService.addInvoice(invoiceCreated);
                    }
                    return registrationRepo.save(regisVar);
                } else{
                    throw  new UserCreationException("Error updating status of registration: status is invalid.");
                }

            } else {
                throw  new UserCreationException("Error updating status of registration, you need an authenticated account to do this action.");
            }
        } catch (Exception e){
            throw  new UserCreationException("Error updating status of registration: "+ e.getMessage());
        }
    }

    @Override
    public List<RegisResDTO> getEnrollOfPlan(UserResDTO authUser, ObjectId planId, List<String> statusList) {
        PlanResDTO plan = new PlanResDTO();
        plan.setPlanId(planId.toString());
        List<Registration> regiss =registrationRepo.findAllByProductInfoAndApprovalStatus(plan.getPlanId(), statusList);
        System.out.println(regiss.size());
        List<RegisResDTO> registrations = regiss.stream().
                map(regis -> regis.convertToRegisResDTO())
                .collect(Collectors.toList());

        return registrations;
    }

    @Override
    public List<RegisResDTO> getAllRegistrationOfOnePlan(UserResDTO authUser, ObjectId planId) {
        PlanResDTO plan = new PlanResDTO();
        plan.setPlanId(planId.toString());
        List<Registration> regiss =registrationRepo.findAllByPlanId(plan.getPlanId());
        System.out.println(regiss.size());
        List<RegisResDTO> registrations = regiss.stream().
                map(regis -> regis.convertToRegisResDTO())
                .collect(Collectors.toList());

        return registrations;
    }

}

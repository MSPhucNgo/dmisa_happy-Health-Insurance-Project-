package com.example.happylife.backendhappylife.controller;

import com.example.happylife.backendhappylife.DTO.ClaimDTO.ClaimResDTO;
import com.example.happylife.backendhappylife.DTO.ContractDTO.ContractCreateDTO;
import com.example.happylife.backendhappylife.DTO.ContractDTO.ContractResDTO;
import com.example.happylife.backendhappylife.DTO.UserDTO.UserResDTO;
import com.example.happylife.backendhappylife.entity.Enum.Role;
import com.example.happylife.backendhappylife.entity.Object.SectionFileCount;
import com.example.happylife.backendhappylife.entity.User;
import com.example.happylife.backendhappylife.service.ContractService;
import com.example.happylife.backendhappylife.service.FireBaseService;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH})

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;
    private final FireBaseService firebaseStorageService;

    public ContractController(FireBaseService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    //API for Manager
    @PostMapping("/create")
    public ResponseEntity<?> addContract(HttpServletRequest request,
                                                      @RequestBody ContractCreateDTO contractCreateDTO) {
        //Contract contract = new Contract();
        //Contract contractUpd = contract.convertResToContract(contractResDTO);
        User userVar = (User) request.getAttribute("userDetails");
        if (userVar.getRole() == Role.INSUARANCE_MANAGER) {
            //Contract savedContract = contractService.updateContractStatus(contractUpd,contractId);
            ContractResDTO contractRes = contractService.addContract(contractCreateDTO); //savedContract.convertToContractResDTO();
            return ResponseEntity.ok(contractRes);
        } else {
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body("You need authenticated account to access this info.");
        }
    }
    //API for Customer
    @PutMapping("/update/{contractId}/status") //API update status của một contract
    public ResponseEntity<?> updateContract(HttpServletRequest request,
                                                         @PathVariable ObjectId contractId,
                                                         @RequestBody ContractResDTO contractResDTO){
        try {
            User userVar = (User) request.getAttribute("userDetails");
            UserResDTO user = userVar.convertFromUserToUserResDTO();
            if(user.getRole() == Role.CUSTOMER){
                return ResponseEntity.ok(contractService.updateContractStatus(contractResDTO,contractId, user));
            }
            else {
                return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body("You need authenticated account to access this info.");
            }
        } catch (Exception e) {
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(e.getMessage());
        }
    };
    @GetMapping("/{userId}") //API get toàn bộ contract theo userId
    public ResponseEntity<?> getByUserId(HttpServletRequest request,
                                         @PathVariable ObjectId userId){
        try {
            User user = (User) request.getAttribute("userDetails");
            UserResDTO userResDTO = user.convertFromUserToUserResDTO();
            if(user.getRole() == Role.CUSTOMER ||
                    user.getRole() == Role.INSUARANCE_MANAGER ||
                    user.getRole() == Role.ACCOUNTANT)
            {
                return ResponseEntity.ok(contractService.getContractByUserId(userId,userResDTO));
            }
            else {
                return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body("You need authenticated account to access this info.");
            }
        } catch (Exception e) {
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(e.getMessage());
        }
    }
    @GetMapping("/{regisId}/getById") //API get 1 regis của một user thông qua regisId
    public ResponseEntity<?> getByRegisId(HttpServletRequest request,
                                          @PathVariable ObjectId regisId){
        try {
            User user = (User) request.getAttribute("userDetails");
            UserResDTO userResDTO = user.convertFromUserToUserResDTO();
            if(user.getRole() == Role.CUSTOMER ||
                    user.getRole() == Role.INSUARANCE_MANAGER ||
                    user.getRole() == Role.ACCOUNTANT)
            {
                return ResponseEntity.ok(contractService.getContractByRegisId(userResDTO,regisId));
            }
            else {
                return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body("You need authenticated account to access this info.");
            }
        } catch (Exception e) {
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(e.getMessage());
        }
    }

    //API for upload image and file
    @PutMapping(value = "/update/{contractId}/image-contentUrl", consumes = "multipart/form-data") // Update contract theo contractId các image ở content
    public ResponseEntity<?> updateContractImageContentUrl(HttpServletRequest request,
                                                        @PathVariable ObjectId contractId,
                                                        @RequestParam("files") MultipartFile[] files) throws IOException {
        try {
            User user = (User) request.getAttribute("userDetails");
            UserResDTO userResDTO = user.convertFromUserToUserResDTO();
            // Lưu các URL của file sau khi upload
            List<String> uploadedUrls = firebaseStorageService.uploadImages(files);
            // Cập nhật thông tin vào Claim và lưu
            ContractResDTO savedContract = contractService.updateContractImageOrFileContentUrl(contractId,uploadedUrls);
            return ResponseEntity.ok(savedContract);
        } catch (Exception e) {
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(e.getMessage());
        }
    };
    @PutMapping(value = "/update/{contractId}/file-docUrl", consumes = "multipart/form-data") // Update Claim theo claimId các files ở DocumentURl
    public ResponseEntity<?> updateContractFileContentUrl(HttpServletRequest request,
                                                       @PathVariable ObjectId contractId,
                                                       @RequestParam("files") MultipartFile[] files) throws IOException {
        try {
            User user = (User) request.getAttribute("userDetails");
            UserResDTO userResDTO = user.convertFromUserToUserResDTO();
            //if(userResDTO)
            // Lưu các URL của file sau khi upload
            List<String> uploadedUrls = firebaseStorageService.uploadFiles(files);
            // Cập nhật thông tin vào Claim và lưu
            ContractResDTO savedContract = contractService.updateContractImageOrFileContentUrl(contractId,uploadedUrls);
            return ResponseEntity.ok(savedContract);
        } catch (Exception e) {
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(e.getMessage());
        }
    };
}

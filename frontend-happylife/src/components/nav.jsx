import {Routes, Route, Link} from 'react-router-dom';
import logoTitle from '../assets/logoTitle.png';
import notification from '../assets/notifications.png';
import logout from '../assets/logout.png';
import Noti from '../components/notification.jsx'

import { useState } from 'react'
import { useNavigate } from 'react-router-dom';
// import { connect } from 'react-redux';
import {useSelector} from 'react-redux'
import { loginSuccess } from '../../redux/authSlice';
import { useDispatch } from 'react-redux';

import { useLocation } from "react-router-dom"

import UserAPI from '../../api/userApi'
import { useEffect } from 'react';

// const navigationRight =[
//  { name: 'Login', href: '/login', current: true },
//  { name: 'Signup', href: '/signup', current: true },
//  {name: 'Profile', href: '/profile', current: true},
// ]



const Nav = () => {
  const location = useLocation()
  const pathname = location.pathname  
  const navigation = [
    { name: 'Home', href: '/home'},
    { name: 'About', href: '/aboutus'},
    { name: 'Plans', href: '/plan'},
    { name: 'Contact', href: '/contact'}, 
   ]

   const navRight = [
      { name: 'Login', href: '/login'},
      { name: 'Signup', href: '/signup'},
    
   ]
  

   const user1 = useSelector((state) =>state.auth.login.currentUser);
   const [realtimeUser, setRealtimeUser] = useState([]);
  console.log('Test: ', user1)
  const navigate = useNavigate()
  const dispatch = useDispatch()

  const handleSignOutBtn = () =>{
    dispatch(loginSuccess(null));
    navigate('/login')
  }
  if(pathname.includes('/staff')) return <></>


  return (
  <div>
  {pathname.includes('login') || pathname.includes('signup') ? 
  (
    <></>
  ):(
  <>
   <nav className=' mx-auto  h-20 bg-custom-blue flex justify-between items-center px-8  border-[0.25px] border-blue-500'>
    <img src={logoTitle} alt="LOGO" className='ml-[226px]'></img>  
    <div className='flex space-x-76px place-content-center text-white font-sans font-medium font text-xl'>
     {navigation.map((item)=>(
       <Link
       key={item.name}
       to={item.href} 
       className='flex justify-center' // underline underline-offset-8 hover:bg-button-blue active:bg-blue-800 focus:outline-none 
       >
      <button
        //onClick={() => handleLeftNavClick(item.name)}
        className={`${pathname.includes(item.href) ? 'focus:outline-none underline underline-offset-8' : ''} px-4 py-2 rounded-lg hover:bg-button-blue active:bg-blue-800`}>
        {item.name}
      </button>
       </Link>
     ))}
    </div>
    <div className="flex space-x-6 text-white font-sans font-medium text-xl items-center">
       {user1 ? (
        <>
            <div className='flex gap-[27px]'>
              <Link
                key='profile'
                to='/profile/information' 
              >
              <button
              //onClick={() => handleRightNavClick(item.name)}
              className={`${user1.token ? '' : ''} bg-button-blue text-white px-4 py-2 rounded items-center rounded-full ml-[100px] z-30`}
              >
                T 
              </button>
              </Link>
            </div>
            <Noti/>
            <button onClick={() => handleSignOutBtn()}>
              <img src={logout}></img>
            </button>
        </>
       ) : 
       (        
       <>

       {navRight.map((item) => (
         <Link key={item.name} to={item.href}>
           {item.name === 'Login' ? (
             <button 
             //onClick={() => handleRightNavClick(item.name)}
             className={`bg-button-blue text-white px-4 py-2 rounded items-center mr-[1px] hover:bg-blue-700 active:bg-blue-800 focus:outline-none`}
             >
             {item.name}
             </button>
           ) : (
             <button 
             key={item.name}
             //onClick={() => handleRightNavClick(item.name)}
             className={`bg-button-white text-white px-4 py-2 rounded items-center mr-[150px] hover:bg-blue-700 active:bg-blue-800 focus:outline-none`}
             >
             {item.name}
           </button>
           ) 
           }
         </Link>
       ))}
       </>
       )}
  </div>
  </nav>
  </>
  )
}
   </div>
 )
 
}
export default Nav;
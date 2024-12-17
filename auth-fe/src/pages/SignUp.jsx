import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { environment } from '../service/environment';
import { useNavigate, useLocation } from 'react-router-dom';
import { FaEye, FaEyeSlash } from 'react-icons/fa';
import axiosInstance from '../utils/AxiosInstance';
import { toast, ToastContainer } from 'react-toastify';
import Spinner from '../components/Spinner';
import { ClipLoader } from 'react-spinners';

const SignUp = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)
    const [error, setError] = useState('');
    const [signInError, setSignInError] = useState('');
    const [verificationMessage, setVerificationMessage] = useState('');
    const navigate = useNavigate();
    const location = useLocation();
    const [isLoading, setIsLoading]=useState(false);

    useEffect(() => {
        // Check for email verification token in URL
        const queryParams = new URLSearchParams(location.search);
        const verificationToken = queryParams.get('token');
        
        if (verificationToken) {
            verifyEmail(verificationToken);
        }
    }, [location]);

    useEffect(()=>{
        if(signInError){
            toast.error(signInError, {
                draggable:false,
                position:'top-right',
                    autoClose:3000,
                    closeOnClick:true,
                    pauseOnHover: false,
                    hideProgressBar:true,
                    pauseOnFocusLoss: false,
                    className:"toast-error"//create css for this
            })
        }
    },[signInError])

    const verifyEmail = async (token) => {
        try {
            
            const response = await axiosInstance.get(`${environment.apiUrl}/auth/verify`, {
                params: { token }
            });
            setVerificationMessage('Email verified successfully! Please proceed to log in.');
            // navigate(`${environment.clientUrl}/login`);
        } catch (error) {
            setError('Email verification failed. Please try again or request a new verification link.');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === 'email') {
            setEmail(value);
        } else if (name === 'password') {
            setPassword(value);
        } else if (name === 'confirmPassword') {
            setConfirmPassword(value);
        }
    };

    const validateEmail = (email) => {
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
        return emailRegex.test(email);
    };

    const validatePassword = (password) => {
        // At least 8 characters, one letter and one number
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
        return passwordRegex.test(password);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setVerificationMessage('');
        setIsLoading(true);

        if (!email || !password) {
            setError('Email and password are required.');
            setIsLoading(false)
            return;
        }

        if (!validateEmail(email)) {
            setError('Please enter a valid email address.');
            return;
        }

        if (!validatePassword(password)) {
            setError('Password must be at least 8 characters long and contain both letters and numbers.');
            return;
        }

        if (password !== confirmPassword) {
            setError('Passwords must match.');
            return;
        }

        const payload = {
            email: email,
            password: password,
        };

        try {
            const response = await axiosInstance.post(`${environment.apiUrl}/auth/signup`, payload);
            setVerificationMessage('Verification email sent. Please check your inbox.');
            
            setEmail('');
            setPassword('');
            setConfirmPassword('');
        } catch (error) {
            console.error("Signup error:", error);
            if (error.response && error.response.status === 409) {
                setSignInError('Email already in use.');
                toast.error(signInError);
            } else {
                setSignInError('Signup failed. Please try again.');
                toast.error(signInError);
            }
        }finally{
            setIsLoading(false);
        }
    };

    const handleOAuthLogin = (provider) => {
        window.location.href = `${environment.apiUrl}/oauth2/authorize/${provider}`;
    };

    return (
        <div className='h-screen flex justify-center items-center'>
            <div className='border-2 border-black rounded-xl p-4 flex flex-col w-1/3'>
                <div>
                    <h1 className='font-bold'>Welcome to masQani</h1>
                    <h4 className='font-thin'>Please sign up to continue</h4>
                </div>
                {error && <p className='text-red-500 mb-4'>{error}</p>}

                <div>
                    <form onSubmit={handleSubmit} className='flex-col flex'>
                        <label>Email</label>
                        <input
                            name='email'
                            id='email'
                            type='email'
                            value={email}
                            onChange={handleChange}
                            className='border rounded p-1 mb-2'
                            required
                        />

                        <label>Password</label>
                        <div className='relative'>
                            <input
                                name='password'
                                id='password'
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={handleChange}
                                className='border rounded p-1 mb-2 w-full'
                                required
                            />
                            <button 
                                type='button'
                                onClick={() => setShowPassword(!showPassword)}
                                className='absolute right-2 top-2'
                            >
                                {showPassword ? <FaEyeSlash /> : <FaEye />}
                            </button>
                        </div>

                        <label>Confirm Password</label>
                        <div className='relative'>
                            <input
                                name='confirmPassword'
                                id='confirmPassword'
                                type={showConfirmPassword ? 'text' : 'password'}
                                value={confirmPassword}
                                onChange={handleChange}
                                className='border rounded p-1 mb-2 w-full'
                                required
                            />
                            <button 
                                type='button'
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                className='absolute right-2 top-2'
                            >
                                {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
                            </button>
                        </div>

                        <button 
                            type='submit' 
                            className='bg-black text-white rounded-lg p-2 mt-2'
                            disabled={isLoading}
                        >
                            {isLoading?(<><span>Signing up</span><ClipLoader size={10} style={{color:'white'}}/></>) :('Sign Up')}
                        </button>
                    </form>
                </div>
                <div className='mt-4 flex flex-col'>
                    <h4 className='font-thin'>Or sign up with:</h4>
                    <button
                        onClick={() => handleOAuthLogin('google')}
                        className='text-black rounded-lg p-2 mr-2 text-start underline'
                    >
                        Google
                    </button>
                </div>
                <div 
                    className='text-end text-sm italic underline hover:cursor-pointer' 
                    onClick={()=>navigate('/login')}
                >
                    Already have an account?
                </div>
            </div>
            {verificationMessage && (
                    <>
                    <div className='inset-0 fixed bg-white h-screen flex justify-center items-center'>
                    <div className='text-green-600 m-2 p-2 border rounded-lg'>
                    {verificationMessage}
                </div>
                    </div>
                    </>
                )}
                {signInError&&(
                    <ToastContainer
                    />
                )}
                {isLoading&&(
                        <Spinner/>
                )}
        </div>
    );
};

export default SignUp;
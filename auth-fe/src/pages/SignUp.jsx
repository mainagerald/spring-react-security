import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { environment } from '../service/environment';
import { useNavigate, useLocation } from 'react-router-dom';
import { FaEye, FaEyeSlash } from 'react-icons/fa';

const SignUp = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const handleOAuth2Redirect = () => {
            const queryParams = new URLSearchParams(location.search);
            const accessToken = queryParams.get('access_token');
            const refreshToken = queryParams.get('refresh_token');

            if (accessToken && refreshToken) {
                localStorage.setItem('access_token', accessToken);
                localStorage.setItem('refresh_token', refreshToken);

                navigate('/', { replace: true });
            }
        };

        handleOAuth2Redirect();
    }, [location, navigate]);

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

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!email || !password) {
            setError('Email and password are required.');
            return;
        }
        if (password.length < 8) {
            setError('Password must be atleast 8 characters long.');
            return;
        }
        if (confirmPassword != password) {
            setError('Passwords must match.')
            return;
        }

        const payload = {
            email: email,
            password: password,
        };

        try {
            const response = await axios.post(`${environment.apiUrl}/signup`, payload);
            console.log("Login response:", response.data);

            const { access_token, refresh_token } = response.data;

            localStorage.setItem('access_token', access_token);
            localStorage.setItem('refresh_token', refresh_token);

            navigate('/');
        } catch (error) {
            console.error("Login error:", error);
            setError('Login failed. Please check your credentials.');
        }
    };

    const handleOAuthLogin = (provider) => {
        window.location.href = `${environment.apiUrl}/oauth2/authorize/${provider}`;
    };

    return (
        <div className='h-screen flex justify-center items-center'>
            <div className='border-2 border-black rounded-xl p-4 flex flex-col w-1/3'>
                <div>
                    <h1 className='font-bold'>Welcome</h1>
                    <h4 className='font-thin'>Please sign up to continue</h4>
                </div>
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
                        <input
                            name='password'
                            id='password'
                            type={showPassword ? 'text' : 'password'}
                            value={password}
                            onChange={handleChange}
                            className='border rounded p-1 mb-2'
                            required
                        />
                        <button onClick={() => setShowPassword(!showPassword)}>
                            {showPassword ? <FaEyeSlash /> : <FaEye />}
                        </button>
                        <label>Confirm Password</label>
                        <input
                            name='confirmPassword'
                            id='confirmPassword'
                            type={showPassword ? 'text' : 'password'}
                            value={confirmPassword}
                            onChange={handleChange}
                            className='border rounded p-1 mb-2'
                            required
                        />
                        <button onClick={() => setShowConfirmPassword(!showConfirmPassword)}>
                            {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
                        </button>
                        {error && <p className='text-red-500'>{error}</p>}

                        <button type='submit' className='bg-black text-white rounded-lg p-2 mt-2'>
                            Sign Up
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
                <div className='text-end text-sm italic underline hover:cursor-pointer' onClick={()=>navigate('/login')}>Already have an account?</div>
            </div>
        </div>
    );
};

export default SignUp;
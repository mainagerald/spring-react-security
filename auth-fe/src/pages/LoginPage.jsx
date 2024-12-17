import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../utils/AuthContext';
import { environment } from '../service/environment';
import { toast, ToastContainer } from 'react-toastify';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();
    const { login, oauthLogin } = useAuth();

    useEffect(() => {
        const handleOAuth2Redirect = () => {
            const queryParams = new URLSearchParams(location.search);
            const accessToken = queryParams.get('access_token');
            const refreshToken = queryParams.get('refresh_token');

            if (accessToken && refreshToken) {
                try {
                    oauthLogin(accessToken, refreshToken);
                    toast.success('Logged in successfully!');
                    setTimeout(() => {
                        navigate('/', { replace: true });
                    }, 1000);
                } catch (error) {
                    setError('OAuth login failed. Please try again.');
                    toast.error(error);
                    console.error('OAuth Login Error:', error);
                }
            }
        };

        handleOAuth2Redirect();
    }, [location, oauthLogin, navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === 'email') {
            setEmail(value);
        } else if (name === 'password') {
            setPassword(value);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!email || !password) {
            setError('Email and password are required.');
            toast.error('Email and password are required.');
            return;
        }

        try {
            await login(email, password);
            toast.success('Logged in successfully!');
            setTimeout(() => {
                navigate('/', { replace: true });
            }, 1000);
        } catch (error) {
            setError('Login failed. Please check your credentials.');
            toast.error('Login failed. Please check your credentials.');
            console.error("Login error:", error);
        }
    };

    const handleOAuthLogin = (provider) => {
        window.location.href = `${environment.apiUrl}/oauth2/authorize/${provider}`;
    };

    return (
        <div className='h-screen flex justify-center items-center bg-gray-100'>
            <div className='border-2 border-gray-300 rounded-xl p-6 flex flex-col w-1/3 bg-white shadow-lg'>
                <div className='text-center mb-4'>
                    <h1 className='font-bold text-2xl'>Welcome</h1>
                    <h4 className='font-thin text-gray-600'>Please sign in to continue</h4>
                </div>
                {error && <p className='text-red-500 mb-4'>{error}</p>}
                <form onSubmit={handleSubmit} className='flex flex-col'>
                    <label className='mb-1'>Email</label>
                    <input
                        name='email'
                        id='email'
                        type='email'
                        value={email}
                        onChange={handleChange}
                        className='border rounded p-2 mb-2 focus:outline-none focus:ring-2 focus:ring-blue-400'
                        required
                    />

                    <label className='mb-1'>Password</label>
                    <input
                        name='password'
                        id='password'
                        type='password'
                        value={password}
                        onChange={handleChange}
                        className='border rounded p-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400'
                        required
                    />

                    <button type='submit' className='bg-blue-600 text-white rounded-lg p-2 hover:bg-blue-700 transition duration-200'>
                        Sign In
                    </button>
                </form>
                <div className='mt-4 flex flex-col'>
                    <h4 className='font-thin text-center mb-2'>Or sign in with:</h4>
                    <button
                        onClick={() => handleOAuthLogin('google')}
                        className='text-blue-600 rounded-lg p-2 mr-2 text-start underline'
                    >
                        Google
                    </button>
                </div>
                <div
                    className='text-center text-sm italic underline hover:cursor-pointer mt-4'
                    onClick={() => navigate('/signup')}
                >
                    Don't have an account?
                </div>
            </div>
            <ToastContainer
                draggable
                position='top-right'
                closeOnClick
                autoClose={2000}
            />
        </div>
    );
};

export default LoginPage;

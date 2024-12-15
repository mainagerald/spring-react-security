import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../utils/AuthContext';
import { environment } from '../service/environment';

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
                    navigate('/', { replace: true });
                } catch (error) {
                    setError('OAuth login failed. Please try again.');
                    console.error('OAuth Login Error:', error);
                }
            }
        };

        handleOAuth2Redirect();
    }, []);

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
            return;
        }

        try {
            await login(email, password);
            navigate('/');
        } catch (error) {
            setError('Login failed. Please check your credentials.');
            console.error("Login error:", error);
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
                    <h4 className='font-thin'>Please sign in to continue</h4>
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
                            type='password'
                            value={password}
                            onChange={handleChange}
                            className='border rounded p-1 mb-2'
                            required
                        />
                        
                        {error && <p className='text-red-500'>{error}</p>}
                        
                        <button type='submit' className='bg-black text-white rounded-lg p-2'>
                            Sign In
                        </button>
                    </form>
                </div>
                <div className='mt-4 flex flex-col'>
                    <h4 className='font-thin'>Or sign in with:</h4>
                    <button
                        onClick={() => handleOAuthLogin('google')}
                        className='text-black rounded-lg p-2 mr-2 text-start underline'
                    >
                        Google
                    </button>
                </div>
                <div 
                    className='text-end text-sm italic underline hover:cursor-pointer' 
                    onClick={() => navigate('/signup')}
                >
                    Don't have an account?
                </div>
            </div>
        </div>
    );
};

export default LoginPage;
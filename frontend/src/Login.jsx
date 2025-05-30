import React, {useState} from 'react';
import axios from 'axios';
import {useNavigate} from "react-router-dom";

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(false);
    const [messages, setMessages] = useState('');

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('/user/login',
                {
                    email,
                    password
                });
            const data = response.data;
            if (data) {
                localStorage.setItem('authorization', data);
                setMessages('Login successful.');
                setError(false);
            } else {
                setMessages('No token in response.');
                setError(true);
            }
            setEmail('');
            setPassword('');
            navigate("/dashboard");
        } catch (err) {
            console.error('Login error:', err.response ? err.response.data : err.message);
            setMessages(err.response && err.response.data ? err.response.data : 'An unexpected error occurred.');
            setError(true);
        }
    };
    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Register</button>
            </form>
            {messages && (
                <p style={{color: error ? 'red' : 'green'}}>{messages}</p>
            )}
        </div>
    );
};


export default Login;
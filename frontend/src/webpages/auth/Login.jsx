import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../../App';

const Login = () => {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(
                `${process.env.REACT_APP_BASE_API_URL}/user/login`,
                { email, password },
                {withCredentials: true}
            );

            if (response.status === 200) {
                login();
                navigate('/dashboard');
            } else {
                setError('Invalid login credentials');
            }
        } catch (err) {
            console.error(err);
            setError('Invalid login credentials');
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            {error && <p className="error-text">{error}</p>}
            <form onSubmit={handleSubmit}>
                <label>
                    Email:
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </label><br />
                <label>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </label><br />
                <button type="submit">Log In</button>
                <br />
                <button type="button" onClick={() => navigate('/register')}>
                    Register
                </button>
            </form>
        </div>
    );
};

export default Login;

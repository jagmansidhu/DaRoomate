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
            <form onSubmit={handleSubmit}>
                <label>
                    Email:
                    <br/>
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </label>
                <br />
                <br />
                <label>
                    Password:
                    <br/>
                    <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </label>
                <br />
                {error && <p style={{ color: "red" }} className="error-text">{error}</p>}
                <button type="submit">Log In</button>
                <br />
                <br />

                <button type="button" onClick={() => navigate('/register')}>
                    Register
                </button>
            </form>
        </div>
    );
};

export default Login;

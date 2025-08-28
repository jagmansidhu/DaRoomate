import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Register = () => {
    const navigate = useNavigate();
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post(`${process.env.REACT_APP_BASE_API_URL}/user/register`, {
                firstName,
                lastName,
                email,
                password
            });
            navigate('/login');
        } catch (err) {
            console.error(err);
            setError('Failed to register. Try again.');
        }
    };

    return (
        <div className="register-container">
            <h2>Register</h2>
            {error && <p className="error-text">{error}</p>}
            <form onSubmit={handleSubmit}>
                <label>
                    First Name:
                    <br/>
                    <input
                        type="text"
                        value={firstName}
                        onChange={e => setFirstName(e.target.value)}
                        required
                    />
                </label><br/>
                <label>
                    Last Name:
                    <br/>
                    <input
                        type="text"
                        value={lastName}
                        onChange={e => setLastName(e.target.value)}
                        required
                    />
                </label><br/>
                <label>
                    Email:
                    <br/>
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </label><br/>
                <label>
                    Password:
                    <br/>
                    <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </label><br/>
                <br/>
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default Register;

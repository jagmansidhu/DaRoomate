import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Register = () => {
    const navigate = useNavigate();
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState("");
    const [error, setError] = useState('');
    const [showPasswordRules, setShowPasswordRules] = useState(false); // ✅ track focus

    const passwordRules = [
        { test: /.{8,}/, message: "At least 8 characters" },
        { test: /[A-Z]/, message: "At least one uppercase letter" },
        { test: /[a-z]/, message: "At least one lowercase letter" },
        { test: /[0-9]/, message: "At least one number" },
        { test: /[@#$%^&+=!]/, message: "At least one special character (@#$%^&+=!)" },
    ];

    function validatePassword(password) {
        for (let rule of passwordRules) {
            if (!rule.test.test(password)) {
                return rule.message;
            }
        }
        return null;
    }

    const handleSubmit = async (e) => {
        e.preventDefault();

        const validationError = validatePassword(password);
        if (validationError) {
            setError(validationError);
            return;
        }

        try {
            await axios.post(`${process.env.REACT_APP_BASE_API_URL}/user/register`, {
                firstName,
                lastName,
                email,
                password
            });
            navigate('/login');
        } catch (err) {
            if (err.response) {
                if (err.response.status === 409) {
                    setError(err.response.data.message || 'User with this email already exists.');
                } else {
                    setError('Failed to register. Please try again later.');
                }
            } else if (err.request) {
                setError('No response from the server. Check your internet connection.');
            } else {
                setError('An unknown error occurred.');
            }
            console.error(err);
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
                        onFocus={() => setShowPasswordRules(true)}   // ✅ show on focus
                        onBlur={() => setShowPasswordRules(false)}   // ✅ hide on blur
                        required
                    />
                </label>

                {/* ✅ Only show when input is focused */}
                {showPasswordRules && (
                    <ul style={{ listStyle: "none", paddingLeft: 0 }}>
                        {passwordRules.map((rule, i) => (
                            <li
                                key={i}
                                style={{
                                    color: rule.test.test(password) ? "green" : "red",
                                    fontSize: "0.9em"
                                }}
                            >
                                {rule.message}
                            </li>
                        ))}
                    </ul>
                )}

                <br/>
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default Register;

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const PasswordReset = () => {
    const [data, setData] = useState(null);
    const [validationError, setValidationError] = useState('');
    const [apiError, setApiError] = useState('');
    const [apiLoading, setApiLoading] = useState(true);
    const [password, setPassword] = useState('');
    const [curPassword, setCurPassword] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [showPasswordRules, setShowPasswordRules] = useState(false);

    const navigate = useNavigate();

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

    useEffect(() => {
        const checkProfileAndFetchData = async () => {
            try {
                const response = await axios.get(
                    `${process.env.REACT_APP_BASE_API_URL}/api/get-user`,
                    { withCredentials: true }
                );
                setData(response.data);
            } catch (err) {
                console.error('Error fetching protected resource:', err);
                setApiError("Failed to load user data.");
            } finally {
                setApiLoading(false);
            }
        };

        checkProfileAndFetchData();
    }, [navigate]);

    const handlePasswordReset = async (e) => {
        e.preventDefault();

        const validationError = validatePassword(password);
        if (validationError) {
            setValidationError(validationError);
            return;
        }

        try {
            const response = await axios.put(
                `${process.env.REACT_APP_BASE_API_URL}/user/updateProfile`,
                {
                    email: data.email,
                    curPassword: curPassword,
                    password: password
                },
                { withCredentials: true }
            );

            if (response.status === 200) {
                setSuccessMessage("Password reset successful ✅");
                setValidationError('');
                setApiError('');

                // redirect after 2 seconds
                setTimeout(() => navigate('/profile'), 2000);
            } else {
                setApiError("Password reset failed.");
            }
        } catch (err) {
            console.error('Error resetting password:', err);
            setApiError("An error occurred while resetting the password.");
        }
    };

    return (
        <div className="container">
            <form className="profile-form" onSubmit={handlePasswordReset}>
                <div className="Label">
                    <label>
                        Email:
                        <input
                            type="email"
                            value={data?.email || ''}
                            disabled
                        />
                    </label>
                </div>

                <br/>
                <div className="Label">
                    <label>
                        Current Password:
                        <input
                            type="password"
                            value={curPassword}
                            onChange={e => setCurPassword(e.target.value)}
                            required
                        />
                    </label>
                </div>

                <br/>
                <div className="Label">
                    <label>
                        New Password:
                        <input
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            onFocus={() => setShowPasswordRules(true)}
                            required
                        />
                    </label>
                </div>

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

                {validationError && <p style={{ color: 'red' }}>{validationError}</p>}
                {apiError && <p style={{ color: 'red' }}>{apiError}</p>}

                <br/>
                <button type="submit" disabled={apiLoading}>Update Profile</button>
            </form>

            {successMessage && (
                <div style={{
                    position: "fixed",
                    bottom: "20px",
                    right: "20px",
                    background: "#4CAF50",
                    color: "white",
                    padding: "12px 20px",
                    borderRadius: "6px",
                    boxShadow: "0px 2px 6px rgba(0,0,0,0.2)",
                    fontSize: "14px"
                }}>
                    {successMessage}
                </div>
            )}
        </div>
    );
};

export default PasswordReset;

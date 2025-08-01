import React, { useEffect, useState } from 'react';
import {jwtDecode} from 'jwt-decode';
import Calendar from '../component/Calendar';
import '../styling/Dashboard.css';

const Dashboard = () => {
    const [email, setEmail] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('jwt');
        if (!token) return;

        try {
            const decoded = jwtDecode(token);
            setEmail(decoded.sub);
        } catch (err) {
            console.error('Failed to decode token:', err);
        }
    }, []);

    if (!email) {
        return (
            <div>
                <h1>Dashboard</h1>
                <p>Please log in to access this resource.</p>
            </div>
        );
    }

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>Dashboard</h1>
                <p>Welcome back, {email}!</p>
            </div>
            <div className="dashboard-content">
                <Calendar />
            </div>
        </div>
    );
};

export default Dashboard;

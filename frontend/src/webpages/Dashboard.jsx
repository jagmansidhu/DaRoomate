import React, { useEffect, useState } from 'react';
import '../styling/Dashboard.css';

const Dashboard = () => {
    const [email, setEmail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [authError, setAuthError] = useState(null); // 🔑 separate error
    const [dataError, setDataError] = useState(null);
    const [chores, setChores] = useState([]);
    const [utilities, setUtilities] = useState([]);

    // Fetch user once
    useEffect(() => {
        const fetchUser = async () => {
            try {
                const res = await fetch(`${process.env.REACT_APP_BASE_API_URL}/user/status`, {
                    method: 'GET',
                    withCredentials: true,
                    credentials: 'include',
                });
                if (!res.ok) throw new Error('Not authenticated');
                const data = await res.json();
                setEmail(data.username || data.email);
            } catch (err) {
                setAuthError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchUser();
    }, []);

    // Fetch chores & utilities once user is known
    useEffect(() => {
        if (!email) return;
        const fetchData = async () => {
            try {
                const [choresRes, utilitiesRes] = await Promise.all([
                    fetch(`${process.env.REACT_APP_BASE_API_URL}/api/chores/upcoming?id=${email}`, {
                        method: 'GET',
                        withCredentials: true,
                        credentials: 'include',
                    }),
                    fetch(`${process.env.REACT_APP_BASE_API_URL}/api/utility/upcoming?id=${email}`, {
                        method: 'GET',
                        withCredentials: true,
                        credentials: 'include',
                    }),
                ]);
                if (choresRes.ok) setChores(await choresRes.json());
                if (utilitiesRes.ok) setUtilities(await utilitiesRes.json());
            } catch (err) {
                setDataError(err.message);
            }
        };
        fetchData();
    }, [email]);

    if (loading) {
        return <div>Loading...</div>;
    }

    // 🔑 Only authError means “log in required”
    if (authError || !email) {
        return (
            <div>
                <h1>Dashboard</h1>
                <p>Please log in to access this resource.</p>
            </div>
        );
    }

    const upcomingChores = chores
        .filter(chore => new Date(chore.dueAt) > new Date())
        .sort((a, b) => new Date(a.dueAt) - new Date(b.dueAt));
    const upcomingUtilities = utilities;

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>Dashboard</h1>
                <p>Welcome back, {email}!</p>
            </div>
            <div className="dashboard-content">
                {/*{dataError && <p className="error">⚠️ Failed to load some data: {dataError}</p>}*/}

                <h2>Upcoming Chores</h2>
                {upcomingChores.length > 0 ? (
                    <ul>
                        {upcomingChores.map(chore => (
                            <li key={chore.id}>
                                {chore.choreName}
                                - Due: {new Date(chore.dueAt).toLocaleDateString()}
                                - Room: {chore.roomName}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No upcoming chores.</p>
                )}

                <h2>Upcoming Utilities</h2>
                {upcomingUtilities.length > 0 ? (
                    <ul>
                        {upcomingUtilities.map(utility => (
                            <li key={utility.id}>
                                {utility.utilityName}
                                - Price: ${utility.utilityPrice}
                                - Room: {utility.roomName}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No upcoming utilities.</p>
                )}
            </div>
        </div>
    );
};

export default Dashboard;

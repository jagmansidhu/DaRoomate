import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../../styling/Rooms.css';

import CreateRoom from '../room/CreateRoom';
import JoinRoom from '../room/JoinRoom';
import RoleManagement from '../room/RoleManage';
import { ROLES } from '../../constants/roles';

const Rooms = () => {
    const navigate = useNavigate(); // <-- hook for navigation

    const [rooms, setRooms] = useState([]);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showJoinModal, setShowJoinModal] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [selectedRoom, setSelectedRoom] = useState(null);
    const [showRoleManagement, setShowRoleManagement] = useState(false);

    useEffect(() => {
        fetchRooms();
    }, []);

    const fetchRooms = async () => {
        try {
            setLoading(true);
            const response = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/rooms`, {
                withCredentials: true,
            });
            setRooms(response.data);
            setError(null);
        } catch (error) {
            console.error('Error fetching rooms:', error);
            setError('Failed to load rooms');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateRoom = (newRoomData) => {
        setRooms([...rooms, newRoomData]);
    };

    const handleRoomJoined = () => {
        fetchRooms();
    };

    const openRoomDetails = (room) => {
        navigate(`/rooms/${room.id}`);
    };

    const openRoleManagement = (room) => {
        setSelectedRoom(room);
        setShowRoleManagement(true);
    };

    if (loading) {
        return (
            <div className="rooms-container">
                <div className="loading">
                    <div className="spinner"></div>
                    <span>Loading rooms...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="rooms-container">
            <div className="rooms-header">
                <h1>My Rooms</h1>
                <p>Manage your shared living spaces</p>
                <div className="rooms-actions">
                    <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
                        Create New Room
                    </button>
                    <button className="btn btn-secondary" onClick={() => setShowJoinModal(true)}>
                        Join Room
                    </button>
                </div>
            </div>

            {error && (
                <div className="alert alert-error">
                    {error}
                    <button onClick={() => setError(null)}>×</button>
                </div>
            )}

            <div className="rooms-grid">
                {rooms.length === 0 ? (
                    <div className="empty-state">No rooms found</div>
                ) : (
                    rooms.map((room) => {
                        const isHeadRoommate = room.members?.some((m) => m.role === ROLES.HEAD_ROOMMATE);
                        const isAssistantRoomate = room.members?.some((m) => m.role === ROLES.ASSISTANT);

                        return (
                            <div key={room.id} className="room-card">
                                <div className="room-header">
                                    <h3>{room.name}</h3>
                                    {isHeadRoommate && <span className="role-badge HEAD_ROOMMATE">Head Roommate</span>}
                                </div>
                                <p className="room-address">{room.address}</p>
                                <p className="room-description">{room.description}</p>
                                <div className="room-members">
                                    <span className="member-count">{room.members?.length || 0} members</span>
                                </div>
                                <div className="room-actions">
                                    <button className="btn btn-primary" onClick={() => openRoomDetails(room)}>
                                        View Details
                                    </button>
                                    {(isHeadRoommate || isAssistantRoomate) && (
                                        <button className="btn btn-secondary" onClick={() => openRoleManagement(room)}>
                                            Manage Roles
                                        </button>
                                    )}
                                </div>
                            </div>
                        );
                    })
                )}
            </div>

            <CreateRoom
                show={showCreateModal}
                onClose={() => setShowCreateModal(false)}
                onCreateRoom={handleCreateRoom}
            />

            <JoinRoom
                show={showJoinModal}
                onClose={() => setShowJoinModal(false)}
                onRoomJoined={handleRoomJoined}
            />

            <RoleManagement
                show={showRoleManagement}
                room={selectedRoom}
                onClose={() => setShowRoleManagement(false)}
                onUpdate={fetchRooms}
            />
        </div>
    );
};

export default Rooms;

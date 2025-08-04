import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import RoomDetailsPage from './RoomDetailsPage';

const RoomDetailsPageWrapper = () => {
    const { roomId } = useParams();
    const navigate = useNavigate();

    const [room, setRoom] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchRoom = async () => {
            try {
                const response = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${roomId}`, {
                    withCredentials: true,
                });
                setRoom(response.data);
            } catch (error) {
                console.error('Failed to load room:', error);
                navigate('/rooms');
            } finally {
                setLoading(false);
            }
        };
        fetchRoom();
    }, [roomId, navigate]);

    const handleClose = useCallback(() => {
        navigate('/rooms');
    }, [navigate]);

    const handleLeaveRoom = useCallback((memberId) => {
        const leaveRoom = async () => {
            try {
                await axios.delete(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${memberId}/leave`, {
                    withCredentials: true,
                });
                navigate('/rooms');
            } catch (error) {
                console.error('Failed to leave room:', error);
            }
        };
        leaveRoom();
    }, [navigate]);

    const handleDeleteRoom = useCallback(() => {
        const deleteRoom = async () => {
            try {
                await axios.delete(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${roomId}/delete-room`, {
                    withCredentials: true,
                });
                navigate('/rooms');
            } catch (error) {
                console.error('Failed to Delete room:', error);
            }
        };
        deleteRoom();

    }, [navigate, roomId]);

    const handleManageRoles = useCallback(() => {
        console.log('Opening role management');
        // TODO: implement actual modal or redirect
    }, []);

    if (loading) return <div>Loading room details...</div>;

    return (
        <RoomDetailsPage
            show={true}
            room={room}
            onClose={handleClose}
            onLeaveRoom={handleLeaveRoom}
            onDeleteRoom={handleDeleteRoom}
            onManageRolesClick={handleManageRoles}
        />
    );
};

export default RoomDetailsPageWrapper;

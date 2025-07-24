import React, { useState } from 'react';
import axios from 'axios';

const JoinRoomModal = ({ show, onClose, onRoomJoined, getAccessTokenSilently }) => {
    const [joinRoomCode, setJoinRoomCode] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        try {
            const accessToken = await getAccessTokenSilently({
                authorizationParams: {
                    audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                    scope: 'write:data',
                },
            });

            await axios.post(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${joinRoomCode}/join`, {}, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });

            onRoomJoined();
            setJoinRoomCode('');
            onClose();
        } catch (err) {
            console.error('Error joining room:', err);
            setError('Failed to join room. Please check the room code.');
        } finally {
            setLoading(false);
        }
    };

    if (!show) return null;

    return (
        <div className="modal-overlay">
            <div className="modal">
                <div className="modal-header">
                    <h2>Join Room</h2>
                    <button className="modal-close" onClick={onClose}>
                        ×
                    </button>
                </div>
                {error && <div className="alert alert-error">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="roomCode">Room Code</label>
                        <input
                            type="text"
                            id="roomCode"
                            value={joinRoomCode}
                            onChange={(e) => setJoinRoomCode(e.target.value)}
                            placeholder="Enter room code"
                            required
                        />
                        <small>Ask the head roommate for the room code</small>
                    </div>
                    <div className="modal-actions">
                        <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>
                            Cancel
                        </button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Joining...' : 'Join Room'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default JoinRoomModal;
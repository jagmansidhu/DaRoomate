import { useState, useEffect } from 'react';
import axios from 'axios';
import { ROLES } from "../../constants/roles";

const RoomDetailsPage = ({
                         show,
                         onClose,
                         room,
                         onLeaveRoom,
                         onDeleteRoom,
                         onManageRolesClick,
                     }) => {
    const [showInviteModal, setShowInviteModal] = useState(false);
    const [inviteEmail, setInviteEmail] = useState('');
    const [inviteStatus, setInviteStatus] = useState('');
    const [user, setUser] = useState(null);

    useEffect(() => {
        const fetchCurrentUser = async () => {
            try {
                const response = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/get-user`, {
                    withCredentials: true,
                });
                setUser(response.data);
            } catch (error) {
                console.error('Error fetching current user:', error);
            }
            console.log("HEHE", room);

        };
        fetchCurrentUser();
    }, []);

    if (!show || !room || !user) return null;

    const memberRole = room.members?.find(m => m.user === user.sub)?.role;
    const isHeadRoommate = memberRole === ROLES.HEAD_ROOMMATE;
    const isAssistantRoommate = memberRole === ROLES.ASSISTANT;

    const handleInviteUser = async () => {
        try {

            const response = await axios.post(
                `${process.env.REACT_APP_BASE_API_URL}/api/rooms/invite`,
                { email: inviteEmail, roomId: room.id },
                {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json',
                        // 'X-CSRF-Token': getCookie("XSRF-TOKEN"),
                    },
                }
        );

            if (response.status === 200) {
                setInviteStatus('Invite sent successfully!');
                setInviteEmail('');
            } else {
                setInviteStatus('Failed to send invite.');
            }
        } catch (error) {
            console.error(error);
            setInviteStatus('Failed to send invite.');
        }
    };

    return (
        <div >
            <div>
                <div className="room-header">
                    <h2>{room.name}</h2>
                </div>

                <div className="room-details">
                    <div className="detail-section">
                        <h3>Room Information</h3>
                        <p><strong>Address:</strong> {room.address}</p>
                        <p><strong>Description:</strong> {room.description}</p>
                        <p><strong>Room Code:</strong> <code>{room.roomCode}</code></p>
                    </div>

                    <div className="detail-section">
                        <h3>Members</h3>
                        <div className="members-list">
                            {room.members?.map((member) => {
                                const isSelf = member.user === user.sub;
                                return (
                                    <div key={member.id} className="member-item">
                                        <div className="member-info">
                                            <span className="member-name">{member.name}</span>
                                            <span className="member-role">{member.role}</span>
                                        </div>
                                        {isSelf && member.role !== ROLES.HEAD_ROOMMATE && (
                                            <button
                                                className="btn btn-danger"
                                                onClick={() => onLeaveRoom(member.id)}
                                            >
                                                Leave Room
                                            </button>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    </div>

                    {(isAssistantRoommate || isHeadRoommate) && (
                        <div className="detail-section">
                            <div className="management-actions">
                                <button className="btn btn-secondary" onClick={() => setShowInviteModal(true)}>
                                    Invite User
                                </button>
                                {isHeadRoommate && (
                                    <div className="management-actions">
                                        <button className="btn btn-danger" onClick={onDeleteRoom}>
                                            Delete Room
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}

                    {showInviteModal && (
                        <div className="modal-overlay">
                            <div className="modal">
                                <div className="modal-header">
                                    <h3>Invite a Roommate</h3>
                                    <button className="modal-close" onClick={() => setShowInviteModal(false)}>×</button>
                                </div>
                                <div className="modal-body">
                                    <input
                                        type="email"
                                        className="input"
                                        placeholder="Enter email address"
                                        value={inviteEmail}
                                        onChange={(e) => setInviteEmail(e.target.value)}
                                    />
                                    {inviteStatus && <p>{inviteStatus}</p>}
                                </div>
                                <div className="modal-actions">
                                    <button className="btn" onClick={() => setShowInviteModal(false)}>Cancel</button>
                                    <button className="btn btn-primary" onClick={handleInviteUser}>Send Invite</button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default RoomDetailsPage;

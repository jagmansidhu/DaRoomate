import {useState, useEffect} from 'react';
import axios from 'axios';
import {ROLES} from "../../constants/roles";

const RoomDetailsPage = ({
                             show, onClose, room, onLeaveRoom, onDeleteRoom, onManageRolesClick,
                         }) => {
    const [showInviteModal, setShowInviteModal] = useState(false);
    const [inviteEmail, setInviteEmail] = useState('');
    const [inviteStatus, setInviteStatus] = useState('');
    const [user, setUser] = useState(null);
    const [showChoreModal, setShowChoreModal] = useState(false);
    const [pendingChores, setPendingChores] = useState([]);
    // Add deadline to choreData
    const [choreData, setChoreData] = useState({
        choreName: '', frequency: 1, frequencyUnit: 'WEEKLY', deadline: ''
    });
    // For remove chores by type
    const [selectedChoreType, setSelectedChoreType] = useState('');
    const [chores, setChores] = useState([]);
    const [showRoleModal, setShowRoleModal] = useState(false);
    const resetChoreData = () => setChoreData({ choreName: '', frequency: 1, frequencyUnit: 'WEEKLY' });

    const addChoreToList = () => {
        if (!choreData.choreName || choreData.frequency < 1 || !isValidDeadline(choreData.deadline)) return;
        setPendingChores([...pendingChores, {...choreData, deadline: new Date(choreData.deadline).toISOString()}]);
        resetChoreData();
    };
    const removeChoreFromList = (idx) => {
        setPendingChores(pendingChores.filter((_, i) => i !== idx));
    };

    const handleSubmitChores = async () => {
        if (pendingChores.length === 0) return;
        try {
            const response = await axios.post(
                `${process.env.REACT_APP_BASE_API_URL}/api/chores/room/createChores/${room.id}`,
                pendingChores,
                {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );
            if (response.status === 200) {
                setShowChoreModal(false);
                setPendingChores([]);
                const choresResponse = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/chores/room/${room.id}`, {
                    withCredentials: true,
                });
                setChores(choresResponse.data);
            }
        } catch (error) {
            console.error('Error creating chores:', error);
        }
    };

    // Helper for deadline validation
    const isValidDeadline = (dateStr) => {
        if (!dateStr) return false;
        const date = new Date(dateStr);
        const now = new Date();
        const max = new Date();
        max.setFullYear(now.getFullYear() + 1);
        return date > now && date <= max;
    };

    // Remove all chores of selected type
    const handleRemoveChoresByType = async () => {
        if (!selectedChoreType) return;
        try {
            await axios.delete(`${process.env.REACT_APP_BASE_API_URL}/api/chores/room/${room.id}/type/${selectedChoreType}`, {
                withCredentials: true,
            });
            // Refresh chores
            const choresResponse = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/chores/room/${room.id}`, {
                withCredentials: true,
            });
            setChores(choresResponse.data);
        } catch (error) {
            console.error('Error removing chores:', error);
        }
    };

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
        };
        const fetchChores = async () => {
            if (room?.id) {
                try {
                    const response = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/chores/room/${room.id}`, {
                        withCredentials: true,
                    });
                    setChores(response.data);
                } catch (error) {
                    console.error('Error fetching chores:', error);
                }
            }
        };
        fetchCurrentUser();
        fetchChores();
    }, [room]);

    // Move all hooks to the top level, before any conditional returns
    const [isCustomChore, setIsCustomChore] = useState(false);

    if (!show || !room || !user) return null;

    const memberRole = room.members?.find(m => m.userId === user.email)?.role;
    const isHeadRoommate = memberRole === ROLES.HEAD_ROOMMATE;
    const isAssistantRoommate = memberRole === ROLES.ASSISTANT;

    const handleInviteUser = async () => {
        try {
            const response = await axios.post(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/invite`, {
                email: inviteEmail,
                roomId: room.id
            }, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json', // 'X-CSRF-Token': getCookie("XSRF-TOKEN"),
                },
            });

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

    // Chore name options
    const CHORE_OPTIONS = ["Broom", "Sweep", "Trash", "Mop", "Vacuum", "Kitchen", "Other"];

    return (<div>
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
                            const isSelf = member.userId === user.email;
                            return (<div key={member.id} className="member-item">
                                <div className="member-info">
                                    <span className="member-name">{member.name}</span>
                                    <span className="member-role">{member.role}</span>
                                </div>
                                {isSelf && member.role !== ROLES.HEAD_ROOMMATE && (<button
                                    className="btn btn-danger"
                                    onClick={() => onLeaveRoom(member.id)}
                                >
                                    Leave Room
                                </button>)}
                            </div>);
                        })}
                    </div>
                    <button className="btn btn-secondary" onClick={() => setShowRoleModal(true)}>
                        Manage Roles
                    </button>
                </div>
                <div className="detail-section">
                    <h3>Chores</h3>
                    <ul style={{display: 'block', padding: 0}}>
                        {(() => {
                            // Filter chores to only show those due within the next month
                            const now = new Date();
                            const oneMonthAhead = new Date();
                            oneMonthAhead.setMonth(now.getMonth() + 1);
                            // Group chores by due date (month/day)
                            const choresByDate = {};
                            chores
                                .filter(chore => {
                                    const dueDate = new Date(chore.dueAt);
                                    return dueDate >= now && dueDate <= oneMonthAhead;
                                })
                                .sort((a, b) => new Date(a.dueAt) - new Date(b.dueAt))
                                .forEach(chore => {
                                    const dueDate = new Date(chore.dueAt);
                                    const key = dueDate.toLocaleString('default', { month: 'short' }) + ' ' + dueDate.getDate();
                                    if (!choresByDate[key]) choresByDate[key] = [];
                                    choresByDate[key].push(chore);
                                });
                            // Render chores grouped by date
                            return Object.entries(choresByDate).map(([date, choresForDate]) => (
                                <li key={date} style={{listStyle: 'none', marginBottom: '0.5rem'}}>
                                    <div style={{background: '#e0e7ff', borderRadius: '6px', padding: '0.25rem 0.5rem', fontWeight: 'bold', fontSize: '0.95rem', minWidth: '60px', textAlign: 'center', marginBottom: '0.25rem', display: 'inline-block'}}>
                                        {date}
                                    </div>
                                    <ul style={{display: 'flex', flexWrap: 'wrap', gap: '0.5rem', padding: 0, margin: 0}}>
                                        {choresForDate.map(chore => (
                                            <li key={chore.id} style={{listStyle: 'none', background: '#f7f7f7', borderRadius: '6px', padding: '0.4rem 0.6rem', minWidth: '90px', display: 'flex', alignItems: 'center', boxShadow: '0 1px 4px rgba(0,0,0,0.04)'}}>
                                                <span style={{fontWeight: 'bold'}}>{chore.choreName}</span>
                                                <span style={{marginLeft: '0.3rem', color: '#555', fontSize: '0.9rem'}}>{chore.choreFrequencyUnitEnum}</span>
                                            </li>
                                        ))}
                                    </ul>
                                </li>
                            ));
                        })()}
                    </ul>
                    <div style={{marginTop: '1rem'}}>
                        <label htmlFor="choreTypeSelect">Remove all chores of type:</label>
                        <select id="choreTypeSelect" value={selectedChoreType} onChange={e => setSelectedChoreType(e.target.value)} style={{marginLeft: '0.5rem'}}>
                            <option value="">Select chore type</option>
                            {[...new Set(chores.map(chore => chore.choreName))].map(name => (
                                <option key={name} value={name}>{name}</option>
                            ))}
                        </select>
                        <button className="btn btn-danger" style={{marginLeft: '0.5rem'}} onClick={handleRemoveChoresByType} disabled={!selectedChoreType}>Remove All</button>
                    </div>
                </div>
                {(isAssistantRoommate || isHeadRoommate) && (
                    <div className="detail-section">
                        <div className="management-actions">
                            <button className="btn btn-secondary" onClick={() => setShowInviteModal(true)}>
                                Invite User
                            </button>
                            <button className="btn btn-primary" onClick={() => setShowChoreModal(true)}>
                                Create Chore List
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
                {showChoreModal && (
                    <div className="modal-overlay">
                        <div className="modal" style={{minWidth: '400px'}}>
                            <div className="modal-header">
                                <h3>Create Chores</h3>
                                <button className="modal-close" onClick={() => setShowChoreModal(false)}>×</button>
                            </div>
                            <div className="modal-body">
                                <div style={{marginBottom: '1rem'}}>
                                    <select
                                        className="input"
                                        value={isCustomChore ? "Other" : choreData.choreName}
                                        onChange={e => {
                                            if (e.target.value === "Other") {
                                                setIsCustomChore(true);
                                                setChoreData({...choreData, choreName: ""});
                                            } else {
                                                setIsCustomChore(false);
                                                setChoreData({...choreData, choreName: e.target.value});
                                            }
                                        }}
                                        style={{marginRight: '0.5rem'}}
                                    >
                                        <option value="">Select chore</option>
                                        {CHORE_OPTIONS.map(opt => (
                                            <option key={opt} value={opt}>{opt}</option>
                                        ))}
                                    </select>
                                    {isCustomChore && (
                                        <input
                                            type="text"
                                            className="input"
                                            placeholder="Custom chore name"
                                            value={choreData.choreName}
                                            onChange={e => setChoreData({...choreData, choreName: e.target.value})}
                                            style={{marginRight: '0.5rem'}}
                                        />
                                    )}
                                    <select
                                        className="input"
                                        value={choreData.frequencyUnit}
                                        onChange={(e) => setChoreData({...choreData, frequencyUnit: e.target.value})}
                                        style={{marginRight: '0.5rem'}}
                                    >
                                        <option value="WEEKLY">Weekly</option>
                                        <option value="BIWEEKLY">Biweekly</option>
                                        <option value="MONTHLY">Monthly</option>
                                    </select>
                                    <input
                                        type="number"
                                        className="input"
                                        placeholder="Frequency (e.g. 1 for once per unit)"
                                        value={choreData.frequency}
                                        min={1}
                                        onChange={(e) => setChoreData({
                                            ...choreData,
                                            frequency: parseInt(e.target.value)
                                        })}
                                        style={{marginRight: '0.5rem', width: '120px'}}
                                    />
                                    <input
                                        type="date"
                                        className="input"
                                        value={choreData.deadline}
                                        onChange={e => setChoreData({...choreData, deadline: e.target.value})}
                                        style={{marginRight: '0.5rem'}}
                                        min={new Date().toISOString().split('T')[0]}
                                        max={(() => { let d = new Date(); d.setFullYear(d.getFullYear() + 1); return d.toISOString().split('T')[0]; })()}
                                    />
                                    <button className="btn btn-success" onClick={addChoreToList} style={{marginLeft: '0.5rem'}} disabled={!choreData.choreName || !isValidDeadline(choreData.deadline)}>Add</button>
                                </div>
                                <div>
                                    <h4>Chores to be created:</h4>
                                    {pendingChores.length === 0 ? <p>No chores added yet.</p> : (
                                        <ul style={{paddingLeft: 0}}>
                                            {pendingChores.map((chore, idx) => (
                                                <li key={idx} style={{listStyle: 'none', marginBottom: '0.5rem', background: '#f7f7f7', padding: '0.5rem', borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
                                                    <span>{chore.choreName} - {chore.frequencyUnit} - {chore.frequency}x - Until: {new Date(chore.deadline).toLocaleDateString()}</span>
                                                    <button className="btn btn-danger" style={{marginLeft: '1rem'}} onClick={() => removeChoreFromList(idx)}>Remove</button>
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </div>
                            </div>
                            <div className="modal-actions">
                                <button className="btn" onClick={() => setShowChoreModal(false)}>Cancel</button>
                                <button className="btn btn-primary" onClick={handleSubmitChores} disabled={pendingChores.length === 0}>Submit All Chores</button>
                            </div>
                        </div>
                    </div>
                )}
                {showRoleModal && (
                    <div className="modal-overlay">
                        <div className="modal">
                            <div className="modal-header">
                                <h3>Manage Roles</h3>
                                <button className="modal-close" onClick={() => setShowRoleModal(false)}>×</button>
                            </div>
                            <div className="modal-body">
                                {/* Role management UI goes here */}
                                <p>Role management functionality coming soon.</p>
                            </div>
                        </div>
                    </div>
                )}
                {showInviteModal && (<div className="modal-overlay">
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
                </div>)}
            </div>
        </div>
    </div>);
};

export default RoomDetailsPage;

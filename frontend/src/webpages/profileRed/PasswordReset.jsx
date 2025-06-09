// import {useAuth0} from "@auth0/auth0-react";
// import React, {useState} from "react";
//
// const PasswordReset = () => {
//     const { user, getAccessTokenSilently } = useAuth0();
//     const [message, setMessage] = useState('');
//     const [error, setError] = useState('');
//     const [isLoading, setIsLoading] = useState(false);
//
//     const handlePasswordReset = async () => {
//         if (!user || !user.email) {
//             setError('Could not determine your email address for password reset. Please log in again.');
//             return;
//         }
//
//         setIsLoading(true);
//         setMessage('');
//         setError('');
//
//         try {
//             const accessToken = await getAccessTokenSilently({
//                 audience: process.env.REACT_APP_AUTH0_AUDIENCE,
//                 scope: "update:profile",
//             });
//
//             const response = await fetch('/api/profile/reset-password', {
//                 method: 'POST',
//                 headers: {
//                     'Content-Type': 'application/json',
//                     'Authorization': `Bearer ${accessToken}`,
//                 },
//                 body: JSON.stringify({ email: user.email }),
//             });
//
//             if (response.ok) {
//                 setMessage('Password reset email sent! Please check your inbox to continue.');
//                 setError('');
//             } else {
//                 let errorMsg = 'Unknown error';
//                 try {
//                     const errorData = await response.json();
//                     errorMsg = errorData.message || errorMsg;
//                 } catch (jsonErr) {
//                     console.error('Response was not JSON:', jsonErr);
//                     const text = await response.text();
//                     console.error('Raw response:', text);
//                 }
//                 setError(`Failed to send password reset email: ${errorMsg}`);
//                 setMessage('');
//             }
//         } catch (err) {
//             console.error('Error sending password reset request:', err);
//             setError('An unexpected error occurred. Please try again.');
//             setMessage('');
//         } finally {
//             setIsLoading(false);
//         }
//
//     };
//
//     return (
//         <div>
//             <h3>Reset Your Password</h3>
//             <p >
//                 Click the button below to send a password reset link to your current email address (<code>{user?.email || 'N/A'}</code>).
//             </p>
//             <button
//                 onClick={handlePasswordReset}
//                 disabled={isLoading}>
//                 {isLoading ? 'Sending...' : 'Send Password Reset Link'}
//             </button>
//             {message && <p>{message}</p>}
//             {error && <p>{error}</p>}
//         </div>
//     );
// };
//
// export default PasswordReset;
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Register from './Register';
import Home from './Home';
import Login from './Login';
import axios from "axios";
import Dashboard from "./Dashboard";

axios.defaults.baseURL = 'http://localhost:8085';
export default function App() {
    return (
            <Router>
                <hr />
                <Routes>
                    <Route path="/home" element={<Home />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/dashboard" element={
                            <Dashboard />
                    } />
                {/*    <Route path="/profile" element={*/}
                {/*        <PrivateRoute>*/}
                {/*            <Profile />*/}
                {/*        </PrivateRoute>*/}
                {/*    } />*/}
                </Routes>
            </Router>
    );
}
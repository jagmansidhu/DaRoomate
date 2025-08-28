import React from 'react';
import { Link } from 'react-router-dom';
import '../styling/Home.css';

const Home = () => {
    return (
        <div className="landing-page">
            {/* Hero Section */}
            <section className="hero-section">
                <div className="hero-content">
                    <div className="hero-text">
                        <h1 className="hero-title">
                            Simplify Your <span className="highlight">Roommate Life</span>
                        </h1>
                        <p className="hero-subtitle">
                            The all-in-one platform for roommates to manage expenses, communicate, 
                            track utilities, and coordinate schedules effortlessly.
                        </p>
                        <div className="hero-buttons">
                            <Link to="/login" className="btn btn-primary btn-lg">
                                Get Started Free
                            </Link>
                            {/* <button className="btn btn-secondary btn-lg">
                                Watch Demo
                            </button> */}
                        </div>
                    </div>
                    <div className="hero-image">
                        <div className="hero-illustration">
                            <div className="roommate-illustration">
                                <div className="roommate-card">
                                    <div className="avatar-group">
                                        <div className="avatar">👥</div>
                                        <div className="avatar">👤</div>
                                        <div className="avatar">👤</div>
                                    </div>
                                    <div className="card-content">
                                        <h3>Roommate Harmony</h3>
                                        <p>Manage everything together</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Features Section */}
            <section className="features-section">
                <div className="container">
                    <div className="section-header">
                        <h2>Everything You Need to Manage Roommates</h2>
                        <p>From splitting bills to coordinating schedules, we've got you covered</p>
                    </div>
                    
                    <div className="features-grid">
                        {/*<div className="feature-card">*/}
                        {/*    <div className="feature-icon">💬</div>*/}
                        {/*    <h3>Messaging & Communication</h3>*/}
                        {/*    <p>Stay connected with your roommates through real-time messaging. Share updates, discuss house matters, and keep everyone in the loop.</p>*/}
                        {/*</div>*/}
                        
                        <div className="feature-card">
                            <div className="feature-icon">💰</div>
                            <h3>Expense Tracking</h3>
                            <p>Track shared expenses, split bills automatically, and keep a clear record of who owes what. Never lose track of payments again.</p>
                        </div>
                        
                        <div className="feature-card">
                            <div className="feature-icon">⚡</div>
                            <h3>Utility Management</h3>
                            <p>Monitor utility bills, set up payment reminders, and ensure everyone contributes their fair share to household expenses.</p>
                        </div>
                        
                        <div className="feature-card">
                            <div className="feature-icon">🏠</div>
                            <h3>Rent Tracking</h3>
                            <p>Keep track of rent payments, due dates, and ensure everyone pays on time. Maintain transparency with payment history.</p>
                        </div>
                        
                        <div className="feature-card">
                            <div className="feature-icon">📅</div>
                            <h3>Shared Calendar</h3>
                            <p>Coordinate schedules, plan events, and avoid conflicts with a shared calendar for all roommates.</p>
                        </div>
                        
                        <div className="feature-card">
                            <div className="feature-icon">📊</div>
                            <h3>Analytics & Reports (Coming Soon)</h3>
                            <p>Get insights into spending patterns, payment history, and household expenses with detailed reports.</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* How It Works Section */}
            <section className="how-it-works-section">
                <div className="container">
                    <div className="section-header">
                        <h2>How It Works</h2>
                        <p>Get started in minutes with our simple setup process</p>
                    </div>
                    
                    <div className="steps-grid">
                        <div className="step-card">
                            <div className="step-number">1</div>
                            <h3>Create Your Household</h3>
                            <p>Set up your household profile and invite your roommates to join the platform.</p>
                        </div>
                        
                        <div className="step-card">
                            <div className="step-number">2</div>
                            <h3>Add Expenses & Bills</h3>
                            <p>Input your shared expenses, utility bills, and rent payments to start tracking.</p>
                        </div>
                        
                        {/*<div className="step-card">*/}
                        {/*    <div className="step-number">3</div>*/}
                        {/*    <h3>Coordinate & Communicate</h3>*/}
                        {/*    <p>Use the messaging system and shared calendar to coordinate with your roommates.</p>*/}
                        {/*</div>*/}
                        
                        <div className="step-card">
                            <div className="step-number">3</div>
                            <h3>Stay Organized</h3>
                            <p>Monitor payments, track expenses, and maintain harmony in your shared living space.</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Future Plans Section */}
            <section className="future-plans-section">
                <div className="container">
                    <div className="section-header">
                        <h2>The Future</h2>
                        <p>We're expanding to make roommate management even better</p>
                    </div>
                    
                    <div className="future-features">
                        <div className="future-feature">
                            <div className="future-icon">🏢</div>
                            <div className="future-content">
                                <h3>Landlord Portal</h3>
                                <p>Landlords will be able to monitor property management, track rent payments, and communicate with tenants through a dedicated portal.</p>
                            </div>
                        </div>
                        
                        <div className="future-feature">
                            <div className="future-icon">📱</div>
                            <div className="future-content">
                                <h3>Mobile App</h3>
                                <p>Access all features on the go with our upcoming mobile application for iOS and Android.</p>
                            </div>
                        </div>
                        
                        <div className="future-feature">
                            <div className="future-icon">🔔</div>
                            <div className="future-content">
                                <h3>Smart Notifications</h3>
                                <p>Get intelligent reminders for bill due dates, rent payments, and important household events.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="cta-section">
                <div className="container">
                    <div className="cta-content">
                        <h2>Ready to Simplify Your Roommate Life?</h2>
                        <p>Join thousands of roommates who are already using our platform to manage their shared living spaces.</p>
                        <div className="cta-buttons">
                            <Link to="/login" className="btn btn-primary btn-lg">
                                Start Managing Today
                            </Link>
                            {/*<Link to="/login" className="btn btn-secondary btn-lg">*/}
                            {/*    Learn More*/}
                            {/*</Link>*/}
                        </div>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="landing-footer">
                <div className="container">
                    <div className="footer-content">
                        <div className="footer-section">
                            <h4>DaROOmate</h4>
                            <p>Making roommate life easier, one feature at a time.</p>
                        </div>
                        <div className="footer-section">
                            <h4>Features</h4>
                            <ul>
                                <li>Messaging</li>
                                <li>Expense Tracking</li>
                                <li>Utility Management</li>
                                <li>Shared Calendar</li>
                            </ul>
                        </div>
                        <div className="footer-section">
                            <h4>Support</h4>
                            <ul>
                                <li>Help Center</li>
                                <li>Contact Us</li>
                                <li>Privacy Policy</li>
                                <li>Terms of Service</li>
                            </ul>
                        </div>
                    </div>
                    <div className="footer-bottom">
                        <p>&copy; 2024 DaROOmate. All rights reserved.</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Home;
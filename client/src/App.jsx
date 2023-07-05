import {useState} from "react";
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Products from "./components/Products";
import Navbar from "./components/Navbar";
import NotFoundPage from "./components/NotFoundPage";
import ManagerDashboard from "./components/ManagerDashboard";
import ExpertDashboard from "./components/ExpertDashboard";
import ClientDashboard from "./components/ClientDashboard";
import {UserRole} from "../enums/UserRole";
import LoginForm from "./components/LoginForm";
import ProtectedRoute from "./utils/ProtectedRoute";
import {UserProfile} from "./components/UserProfile";
import TicketDetails from "./components/TicketDetails";
import ClientProfileForm from "./components/ClientProfileForm";

function App() {
    return (
        <Router>
            <Root/>
        </Router>
    );
}

function Root() {
    const [userInfo, setUserInfo] = useState(null);

    return (
        <Routes>
            <Route path="/" element={
                <ProtectedRoute
                    userInfo={userInfo}
                    setUserInfo={setUserInfo}
                    rolesAllowed={[UserRole.MANAGER, UserRole.EXPERT, UserRole.CLIENT]}
                />}>
                <Route path="" element={<Navbar userInfo={userInfo}/>}>
                    <Route index element={renderDashboard(userInfo ? userInfo.role : "",userInfo)}/>
                    <Route path='/products' element={<Products userRole={userInfo ? userInfo.role : ""}/>}/>
                    <Route path='/profile' element={<UserProfile userInfo={userInfo} setUserInfo={setUserInfo}/>}/>
                    <Route path='/tickets/:ticketId' element={<TicketDetails userInfo={userInfo}/>}/>
                </Route>
            </Route>

            <Route path='/login' element={<LoginForm/>}/>
            <Route path='/register' element={<ClientProfileForm/>}/>
            <Route path='*' element={<NotFoundPage/>}/>
        </Routes>
    );
}

function renderDashboard(userRole,userInfo) {
    switch (userRole) {
        case UserRole.MANAGER:
            return <ManagerDashboard/>;
        case UserRole.EXPERT:
            return <ExpertDashboard userInfo={userInfo}/>;
        case UserRole.CLIENT:
            return <ClientDashboard userInfo={userInfo}/>;
        default:
            return <NotFoundPage/>;
    }
}

export default App;

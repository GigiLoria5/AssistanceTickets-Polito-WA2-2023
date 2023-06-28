import {Button, Col, Container, Row, Spinner} from "react-bootstrap";
import {UserRole} from "../../enums/UserRole";
import {setAccessToken} from "../utils/utils";

function UserProfile({userInfo, setUserInfo}) {

    const renderProfile = (userRole) => {
        switch (userRole) {
            case UserRole.MANAGER:
                return <ManagerProfile userInfo={userInfo}/>;
            case UserRole.EXPERT:
                return <ExpertProfile userInfo={userInfo}/>;
            case UserRole.CLIENT:
                return <ClientProfile userInfo={userInfo}/>;
            default:
                return null;
        }
    }

    const handleLogout = () => {
        setUserInfo(null);
        setAccessToken(null);
    };

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    {
                        userInfo
                            ? (
                                <>
                                    {renderProfile(userInfo.role)}
                                    <Button variant="danger" onClick={handleLogout}>Logout</Button>{' '}
                                </>
                            )
                            : <Spinner animation="border" variant="primary"/>
                    }
                </Col>
            </Row>
        </Container>
    );
}

function ManagerProfile({userInfo}) {
    return (
        <Container className="my-5">
            <Row className="justify-content-center">
                <Col md={8} className="text-center">
                    <h1 className="mb-4">Manager Profile</h1>
                    <div className="p-3">
                        <p className="mb-2"><strong>Name:</strong> {userInfo.name}</p>
                        <p className="mb-2"><strong>Email:</strong> {userInfo.email}</p>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

function ExpertProfile({userInfo}) {
    return (
        <div>
            <h1>Expert Profile</h1>
        </div>
    );
}

function ClientProfile({userInfo}) {
    return (
        <div>
            <h1>Client Profile</h1>
        </div>
    );
}

export {UserProfile};

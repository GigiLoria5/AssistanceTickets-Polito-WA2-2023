import {Button, Col, Container, Row, Spinner, Table} from "react-bootstrap";
import {UserRole} from "../../enums/UserRole";
import {handleApiError, setAccessToken} from "../utils/utils";
import {useEffect, useState} from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";

function UserProfile({userInfo, setUserInfo}) {
    const {StatusAlertComponent, showError} = useStatusAlert();

    const renderProfile = (userRole) => {
        switch (userRole) {
            case UserRole.MANAGER:
                return <ManagerProfile userInfo={userInfo}/>;
            case UserRole.EXPERT:
                return <ExpertProfile userInfo={userInfo} showError={showError}/>;
            case UserRole.CLIENT:
                return <ClientProfile userInfo={userInfo} showError={showError}/>;
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
                                    <StatusAlertComponent/>
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
                        <p className="mb-2"><b>Name:</b> {userInfo.name}</p>
                        <p className="mb-2"><b>Email:</b> {userInfo.email}</p>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

function ExpertProfile({userInfo, showError}) {
    const [expert, setExpert] = useState(null);

    useEffect(() => {
        API.getExpertById(userInfo.id)
            .then(expert => {
                setExpert(expert)
            })
            .catch(err => handleApiError(err, showError))
    }, [])

    return (
        <Container className="my-5">
            <Row className="justify-content-center">
                <Col md={8} className="text-center">
                    <h1 className="mb-4">Expert Profile</h1>
                    {
                        !expert
                            ? <Spinner animation="border" variant="primary"/>
                            : (
                                <>
                                    <div className="p-3">
                                        <p className="mb-2"><b>Name:</b> {userInfo.name}</p>
                                        <p className="mb-2"><b>Email:</b> {userInfo.email}</p>
                                        <p className="mb-2"><b>Country:</b> {expert.country}</p>
                                        <p className="mb-2"><b>City:</b> {expert.city}</p>
                                        <p className="mb-2"><b>Skills</b></p>
                                        <Table responsive>
                                            <thead>
                                            <tr>
                                                <th>Expertise</th>
                                                <th>Level</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {expert.skills.map((skill, index) => (
                                                <tr key={index}>
                                                    <td>{skill.expertise}</td>
                                                    <td>{skill.level}</td>
                                                </tr>
                                            ))}
                                            </tbody>
                                        </Table>
                                    </div>
                                </>
                            )
                    }
                </Col>
            </Row>
        </Container>
    );
}

function ClientProfile({userInfo, showError}) {
    console.log(userInfo);

    return (
        <div>
            <h1>Client Profile</h1>
        </div>
    );
}

export {UserProfile, ExpertProfile, ClientProfile};

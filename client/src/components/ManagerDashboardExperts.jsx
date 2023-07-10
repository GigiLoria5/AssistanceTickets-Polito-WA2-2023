import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";
import {handleApiError} from "../utils/utils";
import {Button, Col, Container, Form, Modal, Row, Spinner, Table} from "react-bootstrap";
import {Expertise} from "../../enums/Expertise";
import {Level} from "../../enums/Level";
import {useNavigate} from "react-router-dom";

function ManagerDashboardExperts() {
    const [experts, setExperts] = useState(null);
    const [showCreateExpert, setShowCreateExpert] = useState(false);
    const [refreshExperts, setRefreshExperts] = useState(true);
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    useEffect(() => {
        if (refreshExperts)
            getAllExperts()
    }, [refreshExperts]);

    const getAllExperts = () => {
        API.getAllExperts()
            .then((response) => {
                    setExperts(response)
                    resetStatusAlert()
                }
            )
            .catch(err => handleApiError(err, showError))
            .finally(_ => {
                setRefreshExperts(false)
            })
    }

    const handleShowCreateExpert = () => setShowCreateExpert(true);
    const handleHideCreateExpert = () => setShowCreateExpert(false);
    const handleCreateExpert = () => {
        handleHideCreateExpert();
        setRefreshExperts(true);
    };

    const searchExpert = (expertId) => {
        API.getExpertById(expertId)
            .then((x) => {
                    setExperts([x])
                    resetStatusAlert()
                }
            )
            .catch(err => handleApiError(err, showError))
    }

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    <h2>Experts Management <Button onClick={handleShowCreateExpert}>Create Expert</Button></h2>
                    <StatusAlertComponent/>
                    {
                        experts || !refreshExperts ?
                            <>
                                <ExpertSearchBar getAllExperts={getAllExperts} searchExpert={searchExpert}/>
                                <CreateExpertModal show={showCreateExpert} handleCreate={handleCreateExpert}
                                                   handleClose={handleHideCreateExpert}/>
                                <ExpertsTable experts={experts}/>
                            </>
                            : <Spinner animation="border" variant="primary"/>
                    }
                </Col>
            </Row>
        </Container>
    );
}

const ExpertSearchBar = ({getAllExperts, searchExpert}) => {
    const [searchValue, setSearchValue] = useState("");

    const handleSearch = () => {
        const trimmedValue = searchValue.trim();
        if (trimmedValue) {
            searchExpert(trimmedValue);
        }
    };

    const handleReset = () => {
        setSearchValue("");
        getAllExperts()
    };

    const handleKeyPress = (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            handleSearch();
        }
    };

    return (
        <Row className="mt-3 w-100 mb-3">
            <Col>
                <Form>
                    <Form.Group>
                        <Form.Control
                            type="search"
                            placeholder="Search by id"
                            value={searchValue}
                            onChange={(event) => setSearchValue(event.target.value)}
                            onKeyDown={handleKeyPress}
                        />
                    </Form.Group>
                </Form>
            </Col>
            <Col xs="auto">
                <Button variant="primary" className="mx-2" onClick={handleSearch}>
                    Search
                </Button>
                <Button variant="secondary" onClick={handleReset}>
                    Reset
                </Button>
            </Col>
        </Row>
    );
}


const ExpertsTable = ({experts}) => {

    const navigate = useNavigate();

    return (
        <Table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Country</th>
                <th>City</th>
                <th>Skills</th>
            </tr>
            </thead>
            <tbody>
            {experts.map((expert) => (
                <tr key={expert.expertId}>
                    <td>{expert.expertId}</td>
                    <td>{expert.name} {expert.surname}</td>
                    <td>{expert.email}</td>
                    <td>{expert.country}</td>
                    <td>{expert.city}</td>
                    <td>
                        {expert.skills.map((skill, index) => (
                            <span key={index}>
                                    {Expertise[skill.expertise]} {Level[skill.level]}<br/>
                                </span>
                        ))}
                    </td>
                    <td>
                        <Button onClick={() => navigate(`/experts/${expert.expertId}`)}>Expert details</Button>
                    </td>
                </tr>
            ))}
            </tbody>
        </Table>
    );
};

const CreateExpertModal = ({show, handleClose, handleCreate}) => {
    const defaultExpert = {
        email: '',
        password: '',
        name: '',
        surname: '',
        country: '',
        city: '',
        skills: [{expertise: '', level: ''}]
    }
    const [expert, setExpert] = useState({...defaultExpert});
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    const handleInputChange = (event, index) => {
        const {name, value} = event.target;
        const skills = [...expert.skills];
        skills[index][name] = value;
        setExpert({...expert, skills});
    };

    const handleAddSkill = () => {
        const hasEmptySkill = expert.skills.some((skill) => skill.expertise === '' || skill.level === '');
        if (!hasEmptySkill) {
            setExpert({...expert, skills: [...expert.skills, {expertise: '', level: ''}]});
        }
    };

    const handleRemoveSkill = (index) => {
        const skills = [...expert.skills];
        skills.splice(index, 1);
        setExpert({...expert, skills});
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        API.createExpert(expert)
            .then(_ => {
                    resetStatusAlert();
                    setExpert({...defaultExpert}); // reset
                    handleCreate();
                }
            )
            .catch(err => handleApiError(err, showError))
    };

    return (
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Create Expert</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className='mb-3' controlId="email">
                        <Form.Label>Email address</Form.Label>
                        <Form.Control type="email" placeholder="Enter email" name="email" value={expert.email}
                                      onChange={(e) => setExpert({...expert, email: e.target.value})} required/>
                    </Form.Group>
                    <Form.Group className='mb-3' controlId="password">
                        <Form.Label>Password</Form.Label>
                        <Form.Control type="password" placeholder="Password" name="password" value={expert.password}
                                      onChange={(e) => setExpert({...expert, password: e.target.value})} required/>
                    </Form.Group>
                    <Form.Group className='mb-3' controlId="name">
                        <Form.Label>Name</Form.Label>
                        <Form.Control type="text" placeholder="Enter name" name="name" value={expert.name}
                                      onChange={(e) => setExpert({...expert, name: e.target.value})} required/>
                    </Form.Group>
                    <Form.Group className='mb-3' controlId="surname">
                        <Form.Label>Surname</Form.Label>
                        <Form.Control type="text" placeholder="Enter surname" name="surname" value={expert.surname}
                                      onChange={(e) => setExpert({...expert, surname: e.target.value})} required/>
                    </Form.Group>
                    <Form.Group className='mb-3' controlId="country">
                        <Form.Label>Country</Form.Label>
                        <Form.Control type="text" placeholder="Enter country" name="country" value={expert.country}
                                      onChange={(e) => setExpert({...expert, country: e.target.value})} required/>
                    </Form.Group>
                    <Form.Group className='mb-3' controlId="city">
                        <Form.Label>City</Form.Label>
                        <Form.Control type="text" placeholder="Enter city" name="city" value={expert.city}
                                      onChange={(e) => setExpert({...expert, city: e.target.value})} required/>
                    </Form.Group>
                    <Form.Group className='mb-3'>
                        <Form.Label>Skills <Button variant="secondary" size="sm" onClick={handleAddSkill}>
                            Add Skill
                        </Button></Form.Label>
                        {expert.skills.map((skill, index) => (
                            <div key={index}>
                                <Form.Control className='mb-3' as="select" name="expertise" value={skill.expertise}
                                              onChange={(event) => handleInputChange(event, index)} required>
                                    <option value="">Select expertise</option>
                                    {Object.keys(Expertise)
                                        .map((key) => (
                                            <option
                                                key={key}
                                                value={key}
                                                disabled={expert.skills.map(s => s.expertise).includes(key)}>
                                                {Expertise[key]}
                                            </option>
                                        ))}
                                </Form.Control>
                                <Form.Control as="select" name="level" value={skill.level}
                                              onChange={(event) => handleInputChange(event, index)} required>
                                    <option value="">Select level</option>
                                    {Object.keys(Level).map((key) => (
                                        <option key={key} value={key}>
                                            {Level[key]}
                                        </option>
                                    ))}
                                </Form.Control>
                                {index > 0 && (
                                    <Button className="mt-3" variant="danger" size="sm"
                                            onClick={() => handleRemoveSkill(index)}>
                                        Remove Skill
                                    </Button>
                                )}
                                <hr/>
                            </div>
                        ))}
                    </Form.Group>
                    <div className="d-flex flex-column align-items-center mt-3">
                        <StatusAlertComponent/>
                        <Button className="w-100" variant="primary" type="submit">
                            Create
                        </Button>
                    </div>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default ManagerDashboardExperts;

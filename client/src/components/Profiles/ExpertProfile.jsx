import {Col, Container, Row, Spinner, Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import API from "../../API";
import {handleApiError} from "../../utils/utils";

function ExpertProfile({expertId, showError}) {
    const [expert, setExpert] = useState(null);

    useEffect(() => {
        API.getExpertById(expertId)
            .then(expert => {
                setExpert(expert)
            })
            .catch(err => handleApiError(err, showError))
    }, [])

    return (
        <Container className="my-5">
            <Row className="justify-content-center">
                <Col md={8} className="text-center">
                    <h2 className='text-center'>Expert Profile</h2>
                    {
                        !expert
                            ? <Spinner animation="border" variant="primary"/>
                            : (
                                <>
                                    <div className="p-3">
                                        <p className="mb-2"><b>Name:</b> {expert.name} {expert.surname}</p>
                                        <p className="mb-2"><b>Email:</b> {expert.email}</p>
                                        <p className="mb-2"><b>Country:</b> {expert.country}</p>
                                        <p className="mb-2"><b>City:</b> {expert.city}</p>
                                        <Table responsive>
                                            <thead>
                                            <tr>
                                                <th>Skill Expertise</th>
                                                <th>Skill Level</th>
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

export default ExpertProfile;

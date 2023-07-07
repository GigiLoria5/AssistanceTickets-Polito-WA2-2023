import {Button, Col, Row, Spinner, Table} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import API from "../API";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import {handleApiError} from "../utils/utils";

function Experts({title, actionName, action}) {
    const [expertsData, setExpertsData] = useState(null);
    const [loading, setLoading] = useState(true)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const [errorPresence, setErrorPresence] = useState(false)


    useEffect(() => {
            getAllExperts()
        }, []
    );
    const getAllExperts = () => {
        API.getAllExperts()
            .then((x) => {
                    setExpertsData(x)
                    setLoading(false)
                    resetStatusAlert()
                }
            )
            .catch(err => stopAnimationAndShowError(err.error))
    }
    const stopAnimationAndShowError = (err) => {
        setLoading(false)
        setErrorPresence(true)
        handleApiError(err,showError)
    }

    /* TODO EVENTUALLY CREATE A SEARCH BAR BY SOMETHING*/

    return (
        <>
            <StatusAlertComponent/>
            {
                loading ?
                    <Spinner animation="border" variant="primary"/>
                    :
                    !errorPresence ?
                        <>
                            <h4>
                                {title}
                            </h4>
                            <Row>
                                <Col>
                                    <ExpertsTable experts={expertsData} actionName={actionName}
                                                  action={action}/>
                                </Col>
                            </Row>
                        </>
                        : null
            }
        </>
    )
}

function ExpertsTable({experts, actionName, action}) {
    return (
        (experts.length) > 0 ?
                <div className="table-responsive">
                    <Table>
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Surname</th>
                            <th>Email</th>
                            <th>Country</th>
                            <th>City</th>
                            <th>Skills</th>
                        </tr>
                        </thead>
                        <tbody>
                        {experts.map((expert) => (
                            <tr key={expert.expertId}>
                                <td>{expert.name}</td>
                                <td>{expert.surname}</td>
                                <td>{expert.email}</td>
                                <td>{expert.country}</td>
                                <td>{expert.city}</td>
                                <td>
                                    {expert.skills.map(
                                        (skill) => (
                                            <Row key={skill.expertise}>
                                                <Col><b>{skill.expertise}</b> ({skill.level})</Col>
                                            </Row>
                                        )
                                    )}
                                </td>
                                {action !== undefined ?
                                    <td>
                                        <Button onClick={() => action(expert)}>
                                            {actionName}
                                        </Button>
                                    </td>
                                    : null
                                }
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                </div>
                : <div>No experts found</div>

    );

}


export default Experts;

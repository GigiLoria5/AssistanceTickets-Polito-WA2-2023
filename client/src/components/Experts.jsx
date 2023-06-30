import {Button, Col, Row, Table} from "react-bootstrap";
import React from "react";

function Experts({experts, title,actionName, action}) {
    /* TODO EVENTUALLY CREATE A SEARCH BAR BY SOMETHING*/
    return (
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
    );

}


export default Experts;

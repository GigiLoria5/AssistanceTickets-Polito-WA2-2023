import {Offcanvas} from "react-bootstrap";
import React from "react";

function ClientInfoCanvas({show, onHide, clientInfo}) {
    return <Offcanvas show={show} onHide={onHide}>
        <Offcanvas.Header closeButton>
            <Offcanvas.Title><h2>Customer Info</h2></Offcanvas.Title>
        </Offcanvas.Header>
        <Offcanvas.Body>
            <p className="fs-4"><b>Name</b><br/>{clientInfo.name}</p>
            <p className="fs-4"><b>Surname</b><br/>{clientInfo.surname}</p>
            <p className="fs-4"><b>Phone</b><br/>{clientInfo.phoneNumber}</p>
            <p className="fs-4"><b>E-mail</b><br/>{clientInfo.email}</p>
        </Offcanvas.Body>
    </Offcanvas>
}

export default ClientInfoCanvas;

import {Offcanvas} from "react-bootstrap";
import React from "react";

function ExpertInfoCanvas({show, onHide, expertInfo}) {
    return <Offcanvas show={show} onHide={onHide}>
        <Offcanvas.Header closeButton>
            <Offcanvas.Title><h2>Expert Info</h2></Offcanvas.Title>
        </Offcanvas.Header>
        <Offcanvas.Body>
            <p className="fs-4"><b>Name</b><br/>{expertInfo.name}</p>
            <p className="fs-4"><b>Surname</b><br/>{expertInfo.surname}</p>
            <p className="fs-4"><b>E-mail</b><br/>{expertInfo.email}</p>
        </Offcanvas.Body>
    </Offcanvas>
}

export default ExpertInfoCanvas;

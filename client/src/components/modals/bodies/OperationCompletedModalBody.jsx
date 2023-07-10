import React, {useContext} from "react";
import {Button, Modal} from "react-bootstrap";
import {CustomModalContext} from "../CustomModal";

function OperationCompletedModalBody({description}) {
    const {handleClose} = useContext(CustomModalContext)
    return (<>
            <Modal.Body>{description}</Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={handleClose}>
                    Close
                </Button>
            </Modal.Footer>
        </>
    )
}

export {OperationCompletedModalBody};

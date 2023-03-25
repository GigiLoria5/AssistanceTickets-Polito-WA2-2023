import {Row, Col, Button, Container,} from "react-bootstrap";
import SearchProfile from "./SearchProfile";
import DisplayProfile from "./DisplayProfile";
import FormProfile from "./FormProfile";
import {useState} from "react";

function Profiles(props) {
    const [isFormVisible,setIsFormVisible] = useState(false);
    const handleClick = () => {
        setIsFormVisible(isFormVisible=>!isFormVisible)
    }

    return (
        <>
        {
            isFormVisible ?
                <FormProfile/>
                :
                <Container style={{maxWidth: "75%"}}>
                    <Row>
                        <Col>
                            <SearchProfile getProfile={props.getProfile}></SearchProfile>
                        </Col>
                        <Col>
                            {'email' in props.profile && <DisplayProfile profile={props.profile}/>}
                        </Col>
                    </Row>

                    <Row className="mt-3 mx-auto" style={{maxWidth: "25%"}}>
                        <Button onClick={handleClick}>Add new profile</Button>
                    </Row>
                </Container>

        }
        </>
    );
}

export default Profiles

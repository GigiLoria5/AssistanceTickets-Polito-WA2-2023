const APIURL = new URL('http://localhost:8080/API/')
async function getProfileByEmail(email){
    const response = await fetch(new URL('profiles/' + email, APIURL), {credentials:'include'})
    const profile =  await response.json()
    if(response.ok)
        return profile
    else
        throw profile
}

function addProfile(profile){
    return new Promise((resolve,reject) => {
        fetch(new URL('profiles/',APIURL), {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify()
        }).then(async (response)=> {
            if(response.ok){
                resolve(null);
            } else {
                reject({error: 'server error ${response.status}'})
            }
        }).catch(()=> reject({error:"cannot communicate withe the server"}));
    });
}

const ProfileAPI = {getProfileByEmail,addProfile}
export default ProfileAPI
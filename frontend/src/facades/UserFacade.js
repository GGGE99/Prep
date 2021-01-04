import { loginURL as base } from "../utils/settings";
import { makeOptions, handleHttpErrors } from "../utils/fetchUtils";

function userFacade() {
  const URL = base + "api/user/";

  const fetchUsers = () => {
    const options = makeOptions("GET", true);
    return fetch(URL + "all", options).then(handleHttpErrors);
  };

  const editRole = (role, username) => {
    const options = makeOptions("POST", true, { role, username });
    return fetch(URL + "edit-role", options).then(handleHttpErrors);
  };

  return {
    fetchUsers,
    editRole,
  };
}

const facade = userFacade();
export default facade;

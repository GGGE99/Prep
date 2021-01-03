import { makeOptions, handleHttpErrors } from "../utils/fetchUtils";
import { loginURL as base } from "../utils/settings";

function apiFacade() {
  const URL = base + "api/"

  const login = (user, password) => {
    const options = makeOptions("POST", true, {
      username: user,
      password: password,
    });
    return fetch(URL + "login", options)
      .then(handleHttpErrors)
  };

  const signup = (username, password) => {
    const options = makeOptions("POST", false, {
      username,
      password,
    });
    return fetch(
      URL + "signup",
      options
    ).then(handleHttpErrors);
  };

  const fetchUserRole = (user) => {
    const options = makeOptions("GET", true);
    return fetch(URL + "user/" + user, options).then(handleHttpErrors);
  };

  return {
    login,
    fetchUserRole,
    signup,
  };
}
const facade = apiFacade();
export default facade;

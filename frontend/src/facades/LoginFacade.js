import { makeOptions, handleHttpErrors, fetcher } from "../utils/fetchUtils";
import { baseURL } from "../utils/settings";

function apiFacade() {
  const URL = baseURL + "api/";

  const login = (user, password) => {
    const options = makeOptions("POST", true, {
      username: user,
      password: password,
    });
    return fetch(URL + "login", options).then(handleHttpErrors);
  };

  const signup = (username, password, action, setError) => {
    const options = makeOptions("POST", false, {
      username,
      password,
    });
    return fetcher(URL + "signup", options, action, setError);
  };

  return {
    login,
    signup,
  };
}
const facade = apiFacade();
export default facade;

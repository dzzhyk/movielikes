const getters = {
  token: state => state.user.token,
  name: state => state.user.name,
  logined: state => state.user.logined,
}
export default getters
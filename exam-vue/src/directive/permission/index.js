import perm from './perm'
import role from './role'

const install = function(Vue) {
  Vue.directive('perm', perm)
  Vue.directive('role', role)
}

if (window.Vue) {
  window['perm'] = perm
  window['role'] = role
  Vue.use(install); // eslint-disable-line
}

role.install = install
export default role

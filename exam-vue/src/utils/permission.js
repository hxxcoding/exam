import store from '@/store'

/**
 * @param {Array} value
 * @returns {Boolean}
 * @example see @/views/permission/directive.vue
 */
export function checkRoles(value) {
  if (value && value instanceof Array && value.length > 0) {
    const roles = store.getters && store.getters.roles
    const needRoles = value

    const hasRoles = roles.some(role => {
      return needRoles.includes(role)
    })

    if (!hasRoles) {
      return false
    }
    return true
  } else {
    return false
  }
}

/**
 *
 * @param value
 * @returns {boolean}
 */
export function checkPerms(value) {
  if (value && value instanceof Array && value.length > 0) {
    const perms = store.getters && store.getters.perms
    const needPerms = value

    const hasPerm = perms.some(perm => {
      return needPerms.includes(perm)
    })

    if (!hasPerm) {
      return false
    }
    return true
  } else {
    return false
  }
}

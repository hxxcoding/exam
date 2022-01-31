import store from '@/store'

export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const perms = store.getters && store.getters.perms

    if (value && value instanceof Array && value.length > 0) {
      const needPerms = value

      const hasPerm = perms.some(perm => {
        return needPerms.includes(perm)
      })

      if (!hasPerm) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`need perms! Like v-perm="['user:list','sys:config:add']"`)
    }
  }
}

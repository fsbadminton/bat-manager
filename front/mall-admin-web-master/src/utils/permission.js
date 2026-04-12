export function hasPermission(permissions, permission) {
  if (!Array.isArray(permissions)) return false
  return permissions.indexOf(permission) !== -1
}


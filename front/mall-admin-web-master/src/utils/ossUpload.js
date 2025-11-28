/**
 * 阿里云 OSS 上传工具模块
 * 用于处理直传签名和上传相关逻辑
 */

/**
 * 生成文件上传的 key（路径）
 * @param {string} dir - OSS 目录前缀
 * @param {File} file - 文件对象
 * @returns {string} 完整的 OSS key
 */
export function generateOSSKey(dir, file) {
  // 生成唯一时间戳
  const timestamp = new Date().getTime();
  // 提取文件扩展名
  const fileExt = file.name.substring(file.name.lastIndexOf('.'));
  // 生成随机数确保唯一性
  const random = Math.floor(Math.random() * 100000000);
  // 组合 key：目录/时间戳_随机数.扩展名
  return `${dir}/${timestamp}_${random}${fileExt}`;
}

/**
 * 获取 OSS 上传的完整 URL
 * @param {string} host - OSS 主机地址
 * @param {string} key - 上传后的文件 key
 * @returns {string} 文件完整 URL
 */
export function getOSSFileUrl(host, key) {
  // 移除末尾斜杠，避免双斜杠
  const baseHost = host.endsWith('/') ? host.slice(0, -1) : host;
  return `${baseHost}/${key}`;
}

/**
 * 验证文件是否符合上传要求
 * @param {File} file - 文件对象
 * @param {number} maxSize - 最大文件大小（字节）
 * @param {Array<string>} allowedTypes - 允许的文件类型（如 ['jpg', 'png']）
 * @returns {Object} 验证结果 { valid: boolean, message: string }
 */
export function validateUploadFile(file, maxSize = 10 * 1024 * 1024, allowedTypes = ['jpg', 'png', 'jpeg']) {
  // 检查文件大小
  if (file.size > maxSize) {
    return {
      valid: false,
      message: `文件大小超过限制（最大 ${maxSize / (1024 * 1024)}MB）`
    };
  }

  // 检查文件类型
  const fileExt = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase();
  if (!allowedTypes.includes(fileExt)) {
    return {
      valid: false,
      message: `不支持的文件格式，仅支持: ${allowedTypes.join(', ')}`
    };
  }

  return {
    valid: true,
    message: '文件验证通过'
  };
}

/**
 * 构建 OSS 上传表单数据
 * @param {Object} ossPolicy - OSS 策略对象（包含 policy, signature, accessKeyId, dir, host）
 * @param {string} key - 文件 key
 * @returns {Object} 表单数据对象
 */
export function buildOSSFormData(ossPolicy, key) {
  return {
    policy: ossPolicy.policy || '',
    signature: ossPolicy.signature || '',
    OSSAccessKeyId: ossPolicy.accessKeyId || '',
    key: key,
    success_action_status: '200',
    // 可选：添加更多 OSS 表单参数
  };
}

/**
 * 处理 OSS 上传成功后的文件 URL
 * @param {string} host - OSS 主机
 * @param {string} key - 文件 key
 * @param {string} filename - 原始文件名（用于显示）
 * @returns {Object} 包含 url 和 filename 的对象
 */
export function handleOSSUploadSuccess(host, key, filename) {
  const url = getOSSFileUrl(host, key);
  return {
    url: url,
    filename: filename || key.split('/').pop(),
    uploadTime: new Date().toISOString()
  };
}

export default {
  generateOSSKey,
  getOSSFileUrl,
  validateUploadFile,
  buildOSSFormData,
  handleOSSUploadSuccess
};

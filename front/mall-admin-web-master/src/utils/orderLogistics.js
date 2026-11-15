export function buildOrderLogisticsRecords(order = {}) {
  const records = []
  if (order.createTime) {
    records.push({ name: '订单已提交', time: order.createTime })
  }
  if (order.status >= 1) {
    records.push({ name: '商家已接单，等待发货', time: order.updateTime || order.createTime || '' })
  }
  if (order.deliveryTime) {
    records.push({
      name: `商家已发货（${order.deliveryCompany || '物流公司待补充'} ${order.deliverySn || ''}）`,
      time: order.deliveryTime
    })
  }
  if (order.returnApplyStatus === 0) {
    records.push({ name: '用户已提交退货申请，等待商家审核', time: order.updateTime || '' })
  }
  if (order.returnApplyStatus === 1) {
    records.push({ name: '商家已同意退货，等待用户寄回商品', time: order.updateTime || '' })
  }
  if (order.returnApplyStatus === 2) {
    records.push({ name: '用户退货已完成，商品已回仓', time: order.updateTime || '' })
  }
  if (order.returnApplyStatus === 3) {
    records.push({ name: '退货申请被驳回，订单继续履约', time: order.updateTime || '' })
  }
  if (order.status === 3) {
    records.push({ name: '用户已确认收货', time: order.updateTime || order.deliveryTime || '' })
  }
  return records.length > 0 ? records : [{ name: '暂无物流信息', time: '' }]
}

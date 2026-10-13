'use strict'

const assert = require('assert')
const fs = require('fs')
const path = require('path')

const projectRoot = path.resolve(__dirname, '..')
const adminPages = [
  'src/views/oms/order/index.vue',
  'src/views/oms/order/orderDetail.vue'
]

function read(relativePath) {
  return fs.readFileSync(path.join(projectRoot, relativePath), 'utf8')
}

function extractMethod(source, methodName) {
  const signature = `${methodName}(order) {`
  const start = source.indexOf(signature)
  assert.notStrictEqual(start, -1, `${methodName} should exist`)

  const bodyStart = start + signature.length
  let depth = 1
  let index = bodyStart
  while (index < source.length && depth > 0) {
    if (source[index] === '{') depth += 1
    if (source[index] === '}') depth -= 1
    index += 1
  }

  assert.strictEqual(depth, 0, `${methodName} should have balanced braces`)
  return new Function('order', source.slice(bodyStart, index - 1))
}

const order = {
  status: 3,
  createTime: '2026-10-01 09:00:00',
  updateTime: '2026-10-03 11:00:00',
  deliveryTime: '2026-10-02 10:00:00',
  deliveryCompany: '顺丰',
  deliverySn: 'SF001'
}

adminPages.forEach(relativePath => {
  const source = read(relativePath)
  const buildLogisticsRecords = extractMethod(source, 'buildLogisticsRecords')

  assert.deepStrictEqual(buildLogisticsRecords(order), [
    { name: '订单已提交', time: order.createTime },
    { name: '商家已接单，等待发货', time: order.updateTime },
    { name: '商家已发货（顺丰 SF001）', time: order.deliveryTime },
    { name: '用户已确认收货', time: order.updateTime }
  ])
  assert.match(source, /:records="logisticsRecords"/)
})

const dialogSource = read('src/views/oms/order/components/logisticsDialog.vue')
assert.doesNotMatch(dialogSource, /2017-04-01/)
assert.match(dialogSource, /:active="logisticsList\.length"/)

console.log('order logistics tests passed')

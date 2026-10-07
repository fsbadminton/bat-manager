<template>
  <div class="detail-container">
    <el-card shadow="never" class="process-guide">
      <span class="font-title-medium">退货处理流程说明</span>
      <el-steps :active="orderReturnApply.status" process-status="process" align-center style="margin-top: 20px">
        <el-step title="待处理" description="用户已提交退货申请">
          <template slot="icon">
            <i class="el-icon-document-copy"></i>
          </template>
        </el-step>
        <el-step title="退货中" description="您同意了退货，等待用户寄回">
          <template slot="icon">
            <i class="el-icon-check"></i>
          </template>
        </el-step>
        <el-step title="已完成" description="已收到返回商品">
          <template slot="icon">
            <i class="el-icon-success"></i>
          </template>
        </el-step>
      </el-steps>
      <div v-if="orderReturnApply.status === 3" style="text-align: center; margin-top: 15px; color: #f56c6c;">
        <i class="el-icon-close"></i> <span>已拒绝此退货申请</span>
      </div>
    </el-card>
    <el-card shadow="never">
      <span class="font-title-medium">退货商品</span>
      <el-table
        border
        class="standard-margin"
        ref="productTable"
        :data="productList">
        <el-table-column label="商品图片" width="160" align="center">
          <template slot-scope="scope">
            <img style="height:80px" :src="scope.row.productPic">
          </template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">
            <span class="font-small">{{scope.row.productName}}</span><br>
            <span class="font-small">品牌：{{scope.row.productBrand}}</span>
          </template>
        </el-table-column>
        <el-table-column label="价格/货号" width="180" align="center">
          <template slot-scope="scope">
            <span class="font-small">价格：￥{{scope.row.productRealPrice}}</span><br>
            <span class="font-small">货号：NO.{{scope.row.productSn}}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="数量" width="100" align="center">
          <template slot-scope="scope">{{scope.row.productCount}}</template>
        </el-table-column>
        <el-table-column label="小计" width="100" align="center">
          <template slot-scope="scope">￥{{totalAmount}}</template>
        </el-table-column>
      </el-table>
      <div style="float:right;margin-top:15px;margin-bottom:15px">
        <span class="font-title-medium">合计：</span>
        <span class="font-title-medium color-danger">￥{{totalAmount}}</span>
      </div>
    </el-card>
    <el-card shadow="never" class="standard-margin">
      <span class="font-title-medium">服务单信息</span>
      <div class="form-container-border">
        <el-row>
          <el-col :span="6" class="form-border form-left-bg font-small">服务单号</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.id}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">申请状态</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.status | formatStatus}}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="form-border form-left-bg font-small" style="height:50px;line-height:30px">订单编号
          </el-col>
          <el-col class="form-border font-small" :span="18" style="height:50px">
            {{orderReturnApply.orderSn}}
            <el-button type="text" size="small" @click="handleViewOrder">查看</el-button>
          </el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">申请时间</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.createTime | formatTime}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">用户账号</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.memberUsername}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">联系人</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiverName}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">联系电话</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiverPhone}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">退货原因</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.reason}}</el-col>
        </el-row>
        
      </div>
      <div class="form-container-border">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">订单金额</el-col>
          <el-col class="form-border font-small" :span="18">￥{{totalAmount}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" style="height:52px;line-height:32px">确认退款金额
          </el-col>
          <el-col class="form-border font-small" style="height:52px" :span="18">
            ￥
            <el-input size="small" v-model="orderReturnApply.returnAmount"
                      :disabled="orderReturnApply.status!==0"
                      style="width:200px;margin-left: 10px"></el-input>
          </el-col>
        </el-row>
        <div v-show="orderReturnApply.status!==3">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" style="height:52px;line-height:32px">选择收货点
          </el-col>
          <el-col class="form-border font-small" style="height:52px" :span="18">
            <el-select size="small"
                       style="width:200px"
                       :disabled="orderReturnApply.status!==0"
                       v-model="orderReturnApply.companyAddress">
              <el-option value="福州商家退货中心" label="福州商家退货中心"></el-option>

            </el-select>
          </el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">收货人姓名</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiverName}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">所在区域</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.address}}</el-col>
        </el-row>
        <!-- <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">详细地址</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.address}}</el-col>
        </el-row> -->
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">联系电话</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiverPhone}}</el-col>
        </el-row>
        </div>
      </div>
      <div class="form-container-border" v-show="orderReturnApply.status!==0">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">处理人员</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.handleMan}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">处理时间</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.handleTime | formatTime}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">处理说明</el-col>
          <el-col class="form-border font-small" :span="18">{{ buildProcessNote(orderReturnApply.status) }}</el-col>
        </el-row>
      </div>
      <div class="form-container-border" v-show="orderReturnApply.status===2">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">收货人员</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiveMan}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" >收货时间</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiveTime | formatTime}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">收货说明</el-col>
          <el-col class="form-border font-small" :span="18">商品已回仓，售后流程完成</el-col>
        </el-row>
      </div>
      <div style="margin-top:15px;text-align: center" v-show="orderReturnApply.status===0">
        <el-button type="primary" size="small" @click="handleUpdateStatus(1)">确认退货</el-button>
        <el-button type="danger" size="small" @click="handleUpdateStatus(3)">拒绝退货</el-button>
      </div>
      <div style="margin-top:15px;text-align: center" v-show="orderReturnApply.status===1">
        <el-button type="primary" size="small" @click="handleUpdateStatus(2)">确认收货</el-button>
      </div>
    </el-card>
  </div>
</template>
<script>
  import {getApplyDetail,updateApplyStatus} from '@/api/returnApply';
  //import {fetchList} from '@/api/companyAddress';
  import {formatDate} from '@/utils/date';

  const defaultUpdateStatusParam = {
    handleMan: 'admin',
    receiveMan: 'admin',
    returnAmount: 0,
    status: 0
  };
  const defaultOrderReturnApply = {
    id: null,
    orderId: null,
    productId: null,
    orderSn: null,
    createTime: null,
    memberUsername: null,
    returnAmount: null,
    returnName: null,
    returnPhone: null,
    status: null,
    handleTime: null,
    productPic: null,
    productSn:null,
    productName: null,
    productBrand: null,
    productAttr: null,
    productCount: null,
    productPrice: null,
    productRealPrice: null,
    reason: null,
    description: null,
    proofPics: null,
    handleMan: null,
    receiveMan: null,
    receiveTime: null
  };
  export default {
    name: 'returnApplyDetail',
    data() {
      return {
        id: null,
        orderReturnApply: Object.assign({},defaultOrderReturnApply),
        productList: null,
        proofPics: null,
        updateStatusParam: Object.assign({}, defaultUpdateStatusParam)
      }
    },
    created() {
      this.id = this.$route.query.id;
      this.getDetail();
    },
    computed: {
      totalAmount() {
        // 以 productList 为准，兼容后端返回 orderItems 或 根级 product 字段
        if (this.productList && this.productList.length > 0) {
          return this.productList.reduce((sum, it) => {
            const price = Number(it.productRealPrice || it.productPrice || 0);
            const count = Number(it.productCount || it.productQuantity || 0);
            return sum + price * count;
          }, 0);
        }
        // fallback to single-root fields
        if (this.orderReturnApply && this.orderReturnApply.productRealPrice && this.orderReturnApply.productCount) {
          return this.orderReturnApply.productRealPrice * this.orderReturnApply.productCount;
        }
        // fallback to order total if present
        if (this.orderReturnApply && this.orderReturnApply.order && this.orderReturnApply.order.totalAmount) {
          return this.orderReturnApply.order.totalAmount;
        }
        return 0;
      }
    },
    filters: {
      formatStatus(status) {
        if (status === 0) {
          return "待处理";
        } else if (status === 1) {
          return "退货中";
        } else if (status === 2) {
          return "已完成";
        } else {
          return "已拒绝";
        }
      },
      formatTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
      },
      formatRegion(address) {
        let str = address.province;
        if (address.city != null) {
          str += "  " + address.city;
        }
        str += "  " + address.region;
        return str;
      }
    },
    methods: {
      handleViewOrder(){
        this.$router.push({path:'/admin/oms/orderDetail',query:{id:this.orderReturnApply.orderId}});
      },
      getDetail() {
        getApplyDetail(this.id).then(response => {
          console.log("getDetail")
          const res = response && response.data ? response.data : response;
          // bind main object
          this.orderReturnApply = res || {};
          // ensure top-level order fields are populated from nested `order` when backend nests them
          if (res.order) {
            // prefer top-level if present, otherwise pick from res.order
            this.orderReturnApply.orderSn = this.orderReturnApply.orderSn || res.order.orderSn;
            this.orderReturnApply.memberUsername = this.orderReturnApply.memberUsername || res.order.memberUsername;
            this.orderReturnApply.orderId = this.orderReturnApply.orderId || res.order.id;
            // copy receiver info if available
            this.orderReturnApply.receiverName = this.orderReturnApply.receiverName || res.order.receiverName || res.order.receiverName || res.order.receiver || null;
            this.orderReturnApply.receiverPhone = this.orderReturnApply.receiverPhone || res.order.receiverPhone || res.order.receiverPhone || res.order.receiverPhone || null;
            this.orderReturnApply.address = this.orderReturnApply.address || res.order.address || res.order.receiverDetailAddress || null;
            // keep nested order object for potential use in template
            this.orderReturnApply.order = res.order;
          }
          // fallback mappings for commonly used fields
          this.orderReturnApply.receiverName = this.orderReturnApply.receiverName || res.receiverName || res.returnUser || null;
          this.orderReturnApply.returnPhone = this.orderReturnApply.returnPhone || res.returnPhone || res.returnPhone || null;
          this.orderReturnApply.reason = this.orderReturnApply.reason || res.reason || null;
          this.orderReturnApply.description = this.orderReturnApply.description || res.description || null;
          this.orderReturnApply.handleMan = this.orderReturnApply.handleMan || res.handleMan || null;
          this.orderReturnApply.handleTime = this.orderReturnApply.handleTime || res.handleTime || null;

          // normalize product list: prefer orderItems, then order.orderItems, then root fields
          let items = [];
          if (Array.isArray(res.orderItems) && res.orderItems.length > 0) {
            items = res.orderItems;
          } else if (res.order && Array.isArray(res.order.orderItems) && res.order.orderItems.length > 0) {
            items = res.order.orderItems;
          } else if (res.productId || res.productName) {
            // single-item fallback
            items = [
              {
                productId: res.productId,
                productName: res.productName || res.productTitle,
                productPic: res.productPic,
                brandName: res.productBrand,
                productPrice: res.productPrice || res.productRealPrice,
                productQuantity: res.productCount || res.productQuantity || 1,
                productRealPrice: res.productRealPrice || res.productPrice
              }
            ];
          }

          // map to view-friendly fields
          this.productList = items.map(item => ({
            productId: item.productId || item.id,
            productName: item.productName || item.productTitle || '',
            productPic: item.productPic || item.productPic,
            productSn:item.productSn,
            productBrand: item.brandName || item.productBrand || '',
            productAttr: item.productAttr || item.attr || '',
            productCount: item.productQuantity || item.productCount || 0,
            productRealPrice: item.productPrice || item.productRealPrice || 0
          }));

          if (this.orderReturnApply.proofPics != null && typeof this.orderReturnApply.proofPics === 'string') {
            this.proofPics = this.orderReturnApply.proofPics.split(",").filter(i => i);
          } else if (Array.isArray(this.orderReturnApply.proofPics)) {
            this.proofPics = this.orderReturnApply.proofPics;
          } else {
            this.proofPics = [];
          }

          // 退货中和完成时预填退款金额/收货点
          if (this.orderReturnApply.status === 1 || this.orderReturnApply.status === 2) {
            this.updateStatusParam.returnAmount = this.orderReturnApply.returnAmount;
          }
        });
      },
      handleUpdateStatus(status){
        this.updateStatusParam.status=status;
        this.updateStatusParam.companyAddress = this.orderReturnApply.companyAddress;
        this.$confirm('是否要进行此操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          updateApplyStatus(this.id,this.updateStatusParam).then(response=>{
            this.$message({
              type: 'success',
              message: '操作成功!',
              duration:1000
            });
            this.$router.back();
          });
        });
      },
      buildProcessNote(status) {
        if (status === 1) return '商家已同意退货，请等待用户寄回商品'
        if (status === 2) return '商家已确认收货，退款/退货流程已结束'
        if (status === 3) return '商家已拒绝本次退货申请'
        return '等待商家审核'
      }
    }
  }
</script>
<style scoped>
  .detail-container {
    position: absolute;
    left: 0;
    right: 0;
    width: 1080px;
    padding: 35px 35px 15px 35px;
    margin: 20px auto;
  }

  .standard-margin {
    margin-top: 15px;
  }
  
  .process-guide {
    background: #f0f9ff;
    border-left: 4px solid #409eff;
    margin-bottom: 20px;
  }
  
  .form-border {
    border-right: 1px solid #DCDFE6;
    border-bottom: 1px solid #DCDFE6;
    padding: 10px;
  }

  .form-container-border {
    border-left: 1px solid #DCDFE6;
    border-top: 1px solid #DCDFE6;
    margin-top: 15px;
  }

  .form-left-bg {
    background: #F2F6FC;
  }
</style>

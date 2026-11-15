<template> 
  <el-dialog title="订单跟踪"
             :visible.sync="visible"
             :before-close="handleClose"
             width="40%">
    <el-steps direction="vertical"
              :active="6"
              finish-status="success"
              space="50px">
      <el-step  v-for="item in logisticsList"
                :key="item.name"
                :title="item.name"
                :description="item.time"></el-step>
    </el-steps>
  </el-dialog>
</template>
<script>
  const defaultLogisticsList = [
    {name: '暂无物流信息', time: ''}
  ];
  export default {
    name:'logisticsDialog',
    props: {
      value: Boolean,
      records: {
        type: Array,
        default: () => []
      }
    },
    computed:{
      visible: {
        get() {
          return this.value;
        },
        set(visible){
          this.value=visible;
        }
      }
    },
    data() {
      return {
        logisticsList: defaultLogisticsList.slice()
      }
    },
    watch: {
      records: {
        immediate: true,
        handler(val) {
          if (Array.isArray(val) && val.length > 0) {
            this.logisticsList = val
          } else {
            this.logisticsList = defaultLogisticsList.slice()
          }
        }
      }
    },
    methods:{
      emitInput(val) {
        this.$emit('input', val)
      },
      handleClose(){
        this.emitInput(false);
      }
    }
  }
</script>
<style></style>

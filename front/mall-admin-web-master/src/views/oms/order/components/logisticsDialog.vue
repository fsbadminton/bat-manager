<template> 
  <el-dialog title="订单跟踪"
             :visible.sync="visible"
             :before-close="handleClose"
             width="40%">
    <el-steps direction="vertical"
              :active="logisticsList.length"
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
          this.$emit('input', visible);
        }
      },
      logisticsList() {
        return Array.isArray(this.records) ? this.records : [];
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


<template> 
  <div>
    <el-upload
      name="file"
      :action="useOss?ossUploadUrl:minioUploadUrl"
      :data="useOss?dataObj:null"
      list-type="picture-card"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :on-remove="handleRemove"
      :on-success="handleUploadSuccess"
      :on-preview="handlePreview"
      :limit="maxCount"
      :on-exceed="handleExceed"
    >
      <i class="el-icon-plus"></i>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>
<script>
  import {policy} from '@/api/oss'
  import {generateOSSKey, validateUploadFile, getOSSFileUrl} from '@/utils/ossUpload'

  export default {
    name: 'multiUpload',
    props: {
      //图片属性数组
      value: Array,
      //最大上传图片数量
      maxCount:{
        type:Number,
        default:5
      }
    },
    data() {
      return {
        dataObj: {
          policy: '',
          signature: '',
          key: '',
          OSSAccessKeyId: '',
          dir: '',
          host: '',
          success_action_status: '200'
        },
        dialogVisible: false,
        dialogImageUrl:null,
        useOss:false, //使用oss->true;使用后端接口->false
        ossUploadUrl:'http://macro-oss.oss-cn-shenzhen.aliyuncs.com',
        // 使用 dev proxy 前缀 /api 转发到后端（见 config/index.js）
        minioUploadUrl:'/api/admin/common/upload',
      };
    },
    computed: {
      fileList() {
        let fileList=[];
        for(let i=0;i<this.value.length;i++){
          fileList.push({url:this.value[i]});
        }
        return fileList;
      }
    },
    methods: {
      emitInput(fileList) {
        let value=[];
        for(let i=0;i<fileList.length;i++){
          value.push(fileList[i].url);
        }
        this.$emit('input', value)
      },
      handleRemove(file, fileList) {
        this.emitInput(fileList);
      },
      handlePreview(file) {
        this.dialogVisible = true;
        this.dialogImageUrl=file.url;
      },
      beforeUpload(file) {
        let _self = this;
        
        // 校验文件
        const validation = validateUploadFile(file, 10 * 1024 * 1024, ['jpg', 'png', 'jpeg']);
        if (!validation.valid) {
          this.$message({
            message: validation.message,
            type: 'warning',
            duration: 2000
          });
          return false;
        }
        
        if(!this.useOss){
          //不使用oss不需要获取策略
          return true;
        }
        
        return new Promise((resolve, reject) => {
          policy().then(response => {
            const res = response && response.data ? response.data : response;
            _self.dataObj.policy = res.policy || '';
            _self.dataObj.signature = res.signature || '';
            _self.dataObj.OSSAccessKeyId = res.accessKeyId || '';
            // 生成唯一的文件 key
            const fileKey = generateOSSKey(res.dir, file);
            _self.dataObj.key = fileKey;
            _self.dataObj.dir = res.dir || '';
            _self.dataObj.host = res.host || '';
            _self.dataObj.success_action_status = '200';
            
            // 保存文件 key 以便上传成功后使用
            _self.uploadingFileKey = fileKey;
            
            resolve(true)
          }).catch(err => {
            console.error('获取 OSS 策略失败:', err)
            this.$message({
              message: '获取上传策略失败，请稍后重试',
              type: 'error',
              duration: 2000
            });
            reject(false)
          })
        })
      },
      handleUploadSuccess(res, file) {
        // 解析后端返回以获取文件 URL，兼容多种返回结构
        let url = '';
        if (this.useOss) {
          url = getOSSFileUrl(this.dataObj.host, this.uploadingFileKey);
        } else {
          try {
            if (!res) {
              url = '';
            } else if (typeof res === 'string') {
              url = res;
            } else if (res.url) {
              url = res.url;
            } else if (res.data) {
              const d = res.data;
              if (typeof d === 'string') {
                url = d;
              } else if (d.url) {
                url = d.url;
              } else if (d.data && d.data.url) {
                url = d.data.url;
              } else if (d.image_url) {
                url = d.image_url;
              } else if (d.path) {
                url = d.path;
              } else if (Array.isArray(d) && d.length > 0 && d[0].url) {
                url = d[0].url;
              }
            } else if (res.data && res.data.data) {
              url = res.data.data.url || '';
            }
          } catch (e) {
            console.warn('解析上传响应失败', e);
            url = '';
          }
        }

        if (!url) {
          this.$message({
            message: '上传成功但未返回图片地址，请检查后端返回格式',
            type: 'warning',
            duration: 2000
          });
          return;
        }

        // 构建新的值数组并 emit 回父组件
        const newVal = Array.isArray(this.value) ? this.value.slice() : [];
        newVal.push(url);
        this.$message({
          message: '文件上传成功',
          type: 'success',
          duration: 1000
        });
        this.$emit('input', newVal);
      },
      handleExceed(files, fileList) {
        this.$message({
          message: '最多只能上传'+this.maxCount+'张图片',
          type: 'warning',
          duration:1000
        });
      },
    }
  }
</script>
<style>

</style>



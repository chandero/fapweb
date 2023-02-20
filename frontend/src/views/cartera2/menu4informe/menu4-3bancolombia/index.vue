<template >
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="24">
          <h1>Informe para Bancolombia</h1>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-row>
        <el-col>
          <el-button :loading="downloadLoading" style="margin-bottom:20px;" type="primary" icon="document" @click="handleDownload" >{{ $t('excel.export') }} excel</el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { InformeBancolombia } from '@/api/informe'

export default {
  data() {
    return {
      downloadLoading: false,
    }
  },
  methods: {
    handleDownload() {
      this.downloadLoading = true
       InformeBancolombia().then(response => {
        var blob = response.data
        const filename = 'Informe_Bancolombia.xlsx'
        if (window.navigator.msSaveOrOpenBlob) {
          window.navigator.msSaveBlob(blob, filename)
        } else {
          var downloadLink = window.document.createElement('a')
          downloadLink.href = window.URL.createObjectURL(new Blob([blob], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }))
          downloadLink.download = filename
          document.body.appendChild(downloadLink)
          downloadLink.click()
          document.body.removeChild(downloadLink)
        }           
        this.downloadLoading = false
       }).catch(() => {
        this.downloadLoading = false
       })
    }
  }
}
</script>

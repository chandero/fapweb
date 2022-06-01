<template>
  <el-container>
    <el-header>
      <h1>DETERIORO</h1>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="tableData" stripe max-height="600">
            <el-table-column label="ID" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.DETERIORO_ID }}</span>
              </template>
            </el-table-column>
            <el-table-column label="TIPO CARTERA" width="150">
              <template slot-scope="scope">
                <span>{{ TipoCartera(scope.row.TIPO_CARTERA_ID) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="EDAD" width="80" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.DETERIORO_EDAD }}</span>
              </template>
            </el-table-column>
            <el-table-column label="TASA" width="80" align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.DETERIORO_TASA }}</span>
              </template>
            </el-table-column>
            <el-table-column label="FECHA REGISTRO" width="180" align="center">
              <template slot-scope="scope">
                <span>{{
                  scope.row.DETERIORO_FECHA_REGISTRO | moment("YYYY-MM-DD")
                }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { getDeterioro } from "@/api/pe";
import { gTipoCartera } from "@/utils/global_functions";

export default {
  data() {
    return {
      tableData: [],
    };
  },
  mounted() {
    getDeterioro().then((res) => {
      this.tableData = res.data;
    });
  },
  methods: {
    TipoCartera(id) {
      return gTipoCartera(id);
    },
  },
};
</script>
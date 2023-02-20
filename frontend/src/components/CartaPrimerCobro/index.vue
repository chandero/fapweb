<template>
  <el-container>
    <el-main>
      <el-row>
        <el-col :span="12">
          Lista con Email No Válido
          <el-table :data="invalidData" highlight-current-row style="width: 90%; font-size: 12px" max-height="400"
            stripe>
            <el-table-column type="index" width="50"> </el-table-column>
            <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                    scope.row.id_colocacion
                }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nombre" prop="nombre" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.codeudor }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Email" prop="email" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.email }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="info" icon="el-icon-download" @click="handleDownload(invalidData)"
            style="margin: 0 0 20px 20px">Exportar a Excel</el-button>
        </el-col>
        <el-col :span="12">
          Lista con Email Válido
          <el-table :data="readyData" highlight-current-row style="width: 90%; font-size: 12px" max-height="400" stripe>
            <el-table-column type="index" width="50"> </el-table-column>
            <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                    scope.row.id_colocacion
                }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nombre" prop="nombre" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.codeudor }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Email" prop="email" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.email }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="info" icon="el-icon-download" @click="handleDownload(readyData)"
            style="margin: 0 0 20px 20px">Exportar a Excel</el-button>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-button type="primary" @click="sendEmails" :disabled="!readyData.length">
            Enviar Emails
          </el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { validEmail } from "@/utils/validate";
import { cartaPrimerAviso } from '@/api/controlcobro';

export default {
  name: "CartaPrimerCobroComponent",
  props: {
    tableData: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      readyData: [],
      invalidData: [],
    };
  },
  mounted() {
    this.readyData = []
    this.invalidData = []
    this.tableData.forEach((item) => {
      if (this.validarEmail(item.email)) {
        this.readyData.push({
          id_colocacion: item.id_colocacion,
          codeudor: item.codeudor,
          deudor: item.deudor,
          email: item.email,
          tipo: item.tipo,
          telefono: item.telefono,
        });
      } else {
        this.invalidData.push({
          id_colocacion: item.id_colocacion,
          codeudor: item.codeudor,
          deudor: item.deudor,
          email: item.email,
          tipo: item.tipo,
          telefono: item.telefono,
        });
      }
    });
  },
  methods: {
    validarEmail(email) {
      if (email.includes("clientessin")) {
        return false;
      } else if (!validEmail(email)) {
        return false;
      } else {
        return true;
      }
    },
    sendEmails() {
      const _promise = async () => {
        for (const item of this.readyData) {
          await cartaPrimerAviso(item.id_colocacion, item.deudor, item.codeudor, item.tipo, item.email);
        }
      }
      _promise().then(() => {
        this.$emit("cerrarCartaPrimerCobroEvent")
      })
    },
    handleDownload(table) {
      this.downloadLoading = true;
      import("@/vendor/Export2Excel").then((excel) => {
        const tHeader = ["Colocacion", "Nombre", "Email", "Teléfono"];
        excel.export_json_to_excel({
          header: tHeader,
          table,
          filename: "Relacion.xlsx",
          autoWidth: true,
          bookType: "xlsx",
        });
        this.downloadLoading = false;
      });
    },
  },
};
</script>

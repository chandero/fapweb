<template>
  <el-container>
    <el-header>
      <h2>Carga de Información para Procesamiendo de Perdida Esperada</h2>
    </el-header>
    <el-main>
      <el-container>
        <el-form size="medium">
          <el-row :gutter="4">
            <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
              <el-form-item label="Año">
                <el-input v-model="anho" type="number" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
              <el-form-item label="Mes">
                <el-select
                  style="width: 100%"
                  ref="mes"
                  v-model="mes"
                  name="meses"
                  :placeholder="$t('mes_select')"
                >
                  <el-option
                    v-for="m in months"
                    :key="m.id"
                    :label="$t('months.' + m.label)"
                    :value="m.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="4">
            <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
              <el-form-item label="Archivo Usuario">
                <el-upload
                  class="upload-demo"
                  ref="upload1"
                  name="fileusuario"
                  :action="action01()"
                  :auto-upload="true"
                  :on-success="f01_ok"
                  :on-error="f01_error"
                >
                  <el-button size="small" type="primary"
                    >Clic para subir archivo
                  </el-button>
                  <div slot="tip" class="el-upload__tip">
                    Solo archivo plano csv separado por punto y coma
                  </div>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
              <el-form-item label="Archivo Cartera">
                <el-upload
                  class="upload-demo"
                  ref="upload2"
                  name="filecartera"
                  :action="action02()"
                  :auto-upload="true"
                  :on-success="f02_ok"
                  :on-error="f02_error"
                >
                  <el-button size="small" type="primary"
                    >Clic para subir archivo</el-button
                  >
                  <div slot="tip" class="el-upload__tip">
                    Solo archivo plano csv separado por punto y coma
                  </div>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
              <el-form-item label="Archivo Deposito">
                <el-upload
                  class="upload-demo"
                  ref="upload3"
                  name="filedeposito"
                  :action="action03()"
                  :auto-upload="true"
                  :on-success="f03_ok"
                  :on-error="f03_error"
                >
                  <el-button size="small" type="primary"
                    >Clic para subir archivo</el-button
                  >
                  <div slot="tip" class="el-upload__tip">
                    Solo archivo plano csv separado por punto y coma
                  </div>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
              <el-form-item label="Archivo Aporte">
                <el-upload
                  class="upload-demo"
                  ref="upload4"
                  name="fileaporte"
                  :action="action04()"
                  :auto-upload="true"
                  :on-success="f04_ok"
                  :on-error="f04_error"
                >
                  <el-button size="small" type="primary"
                    >Clic para subir archivo</el-button
                  >
                  <div slot="tip" class="el-upload__tip">
                    Solo archivo plano csv separado por punto y coma
                  </div>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-row>
          <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
            <el-button
              :disabled="
                anho === null ||
                mes === null ||
                nofile01 ||
                nofile02 ||
                nofile03 ||
                nofile04
              "
              style="margin-left: 10px"
              size="small"
              type="success"
              @click="handleLoad"
              >Cargar Datos</el-button
            >
          </el-col>
        </el-row>
      </el-container>
      <el-row>
        <el-col>
          <el-table
            :data="tableData"
            stripe
            :row-class-name="tachado"
            style="width: 100%"
          >
            <el-table-column :label="$t('anho')" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.CONTROL_CARGA_ANHO
                }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('mes')" width="120">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.CONTROL_CARGA_PERIODO
                }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('fecha')" width="180">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.CONTROL_CARGA_FECHA | moment("YYYY-MM-DD HH:mm")
                }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('registros')" width="100">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.CONTROL_CARGA_REGISTROS
                }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('estado')" width="100">
              <template slot-scope="scope">
                <i
                  style="font-size: 20px"
                  :class="
                    scope.row.CONTROL_CARGA_ESTADO
                      ? 'el-icon-circle-check'
                      : 'el-icon-circle-close'
                  "
                />
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              :label="$t('table.accion')"
              width="140"
            >
              <template slot-scope="scope">
                <!--                 <el-button
                  :disabled="scope.row.controlcarga_estado === 9"
                  size="mini"
                  circle
                  type="success"
                  title="Ver"
                  @click="handleView(scope.$index, scope.row)"
                  ><i class="el-icon-view"></i
                ></el-button>
 -->
                <el-button
                  :disabled="
                    scope.row.controlcarga_procesado ||
                    scope.row.controlcarga_estado === 9
                  "
                  size="mini"
                  circle
                  type="danger"
                  title="Borrar"
                  @click="handleDelete(scope.$index, scope.row)"
                  ><i class="el-icon-delete"></i
                ></el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-size="page_size"
            layout="sizes, prev, pager, next, total"
            :total="total"
          >
          </el-pagination>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { mapGetters } from "vuex";
import EventBus from "@/utils/eventBus";
import { getTodos, eliminar } from "@/api/carga";

import { loadData } from "@/api/pe";
// let msgServer

export default {
  data() {
    return {
      porcentaje: 0,
      inProgress: false,
      anho: new Date().getFullYear(),
      mes: new Date().getMonth() + 1,
      fu01_ok: false,
      fu02_ok: false,
      fu03_ok: false,
      fu04_ok: false,
      nofile01: true,
      nofile02: true,
      nofile03: true,
      nofile04: true,
      tableData: [],
      page_size: 10,
      current_page: 1,
      filter: "",
      order_by: "",
      total: 0,
    };
  },
  computed: {
    ...mapGetters(["baseurl", "empresa", "sessionUUID", "months"]),
  },
  beforeMount() {
    EventBus.$on("loadEnd", () => {
      this.getCargas();
    });
  },
  methods: {
    handleView(index, row) {},
    handleDelete(index, row) {
      this.$confirm(
        "Se borraran de forma permanente los datos. Continuar?",
        "Advertencia",
        {
          confirmButtonText: "Si",
          cancelButtonText: "No",
          type: "warning",
        }
      ).then(() => {
        eliminar(row.CONTROL_CARGA_ANHO, row.CONTROL_CARGA_PERIODO)
          .then((response) => {
            if (response.data === true) {
              this.$notify({
                title: "Exito!",
                message: "Datos eliminados",
                type: "success",
              });
              this.getCargas();
            } else {
              this.$notify.error({
                title: "Error",
                message: "Error al eliminar los datos",
              });
            }
          })
          .catch((e) => {
            this.$notify.error({
              title: "Error",
              message: "Error en el proceso: " + e,
            });
          });
      });
    },
    handleLoad(index, row) {
      this.$confirm(
        "Se cargaran los datos en los archivos. Continuar?",
        "Advertencia",
        {
          confirmButtonText: "Si",
          cancelButtonText: "No",
          type: "warning",
        }
      ).then(() => {
        loadData(this.anho, this.mes, this.sessionUUID)
          .then((response) => {
            if (response.data === true) {
              this.$notify({
                title: "Exito!",
                message: "Cargando Datos En Proceso...",
                type: "success",
              });
              this.getCargas();
            } else {
              this.$notify.error({
                title: "Error",
                message: "Error al cargar los datos",
              });
            }
          })
          .catch((e) => {
            this.$notify.error({
              title: "Error",
              message: "Error al procesar los datos: " + e,
            });
          });
      });
    },
    handleSizeChange(val) {
      this.page_size = val;
      this.getCargas();
    },
    handleCurrentChange(val) {
      this.current_page = val;
      this.getCargas();
    },
    tachado({ row, index }) {
      if (row.controlcarga_estado === 9) {
        return "tachado-row";
      }
      return "";
    },
    action01() {
      return this.baseurl.url + "/ud01/" + this.anho + "/" + this.mes;
    },
    action02() {
      return this.baseurl.url + "/ud02/" + this.anho + "/" + this.mes;
    },
    action03() {
      return this.baseurl.url + "/ud03/" + this.anho + "/" + this.mes;
    },
    action04() {
      return this.baseurl.url + "/ud04/" + this.anho + "/" + this.mes;
    },
    f01_ok(response, file, list) {
      this.fu01_ok = true;
      this.nofile01 = true;
      if (response && file) {
        this.nofile01 = false;
        this.$message({
          message: "Archivo Usuario subido al servidor",
          type: "success",
        });
      }
    },
    f01_error(err, file) {
      this.fu01_ok = false;
      this.nofile01 = true;
      if (err && file) {
        this.$message({
          message: "Error al cargar el archivo Usuario.",
          type: "warning",
        });
      }
    },
    f02_ok(response, file, list) {
      this.fu02_ok = true;
      this.nofile02 = true;
      if (response && file) {
        this.nofile02 = false;
        this.$message({
          message: "Archivo Cartera subido al servidor",
          type: "success",
        });
      }
    },
    f02_error(err, file) {
      this.fu02_ok = false;
      this.nofile02 = true;
      if (err && file) {
        this.$message({
          message: "Error al cargar el archivo Cartera.",
          type: "warning",
        });
      }
    },
    f03_ok(response, file, list) {
      this.fu03_ok = true;
      this.nofile03 = true;
      if (response && file) {
        this.nofile03 = false;
        this.$message({
          message: "Archivo Deposito subido al servidor",
          type: "success",
        });
      }
    },
    f03_error(err, file) {
      this.fu03_ok = false;
      this.nofile03 = true;
      if (err && file) {
        this.$message({
          message: "Error al cargar el archivo Deposito.",
          type: "warning",
        });
      }
    },
    f04_ok(response, file, list) {
      this.fu04_ok = true;
      this.nofile04 = true;
      if (response && file) {
        this.nofile04 = false;
        this.$message({
          message: "Archivo Aporte subido al servidor",
          type: "success",
        });
      }
    },
    f04_error(err, file) {
      this.fu04_ok = false;
      this.nofile04 = true;
      if (err && file) {
        this.$message({
          message: "Error al cargar el archivo Aporte.",
          type: "warning",
        });
      }
    },
    getCargas() {
      getTodos(
        this.page_size,
        this.current_page,
        this.filter,
        this.order_by,
        1
      ).then((response) => {
        this.tableData = response.data;
      });
    },
  },
  mounted() {
    this.getCargas();
  },
  beforeDestroy() {
    // msgServer.close()
  },
};
</script>
<style>
.el-table .tachado-row {
  text-decoration: line-through red solid;
}

.el-progress-bar .el-progress-bar__outer {
  background-color: #8a834a;
}
</style>
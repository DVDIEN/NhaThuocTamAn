<template>
  <!-- ✅ Quảng cáo phụ dưới banner -->
  <!-- ✅ Quảng cáo phụ có carousel bên trái -->
  <section class="container my-4">
    <div class="row g-3">
      <!-- Carousel trái -->
      <div class="col-lg-8">
        <div
          id="adCarousel"
          class="carousel slide rounded shadow-sm overflow-hidden"
          data-bs-ride="carousel"
        >
          <div class="carousel-inner">
            <div class="carousel-item active">
              <img
                src="https://nhathuocthanthien.com.vn/wp-content/uploads/2022/09/dgm_nttt_banner-ca-gai-leo-1100-550px.jpg"
                class="d-block w-100"
                alt="Quảng cáo 1"
              />
            </div>
            <div class="carousel-item">
              <img
                src="https://nhathuocthanthien.com.vn/wp-content/uploads/2024/12/dgm_nttt_fitobimbi-sonno-gocce-banner.jpg"
                class="d-block w-100"
                alt="Quảng cáo 2"
              />
            </div>
          </div>
          <button
            class="carousel-control-prev"
            type="button"
            data-bs-target="#adCarousel"
            data-bs-slide="prev"
          >
            <span class="carousel-control-prev-icon"></span>
            <span class="visually-hidden">Trước</span>
          </button>
          <button
            class="carousel-control-next"
            type="button"
            data-bs-target="#adCarousel"
            data-bs-slide="next"
          >
            <span class="carousel-control-next-icon"></span>
            <span class="visually-hidden">Tiếp</span>
          </button>
        </div>
      </div>

      <!-- 2 ảnh nhỏ bên phải -->
      <div class="col-lg-4 d-flex flex-column gap-3">
        <img
          src="https://nhathuocthanthien.com.vn/wp-content/uploads/2022/09/dgm_nttt_banner-hoat-huyet-thong-mach-tw3-1.jpg"
          class="img-fluid rounded shadow-sm"
          alt="Quảng cáo phụ 1"
        />
        <img
          src="https://nhathuocthanthien.com.vn/wp-content/uploads/2021/02/dgm_nttt_Banner-6-Alipas-mb.jpg"
          class="img-fluid rounded shadow-sm"
          alt="Quảng cáo phụ 2"
        />
      </div>
    </div>
  </section>
</template>

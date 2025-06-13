<template>
  <div id="bannerCarousel" class="carousel slide" data-bs-ride="carousel">
    <div class="carousel-inner">
      <div class="carousel-item active">
        <img
          src="https://cdn.nhathuoclongchau.com.vn/unsafe/1920x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/Dr_papie_thang4_Homepage_PC_1360x416_325a68d833.jpg"
          class="d-block w-100"
          alt="Banner 1"
        />
      </div>
      <div class="carousel-item">
        <img
          src="https://cdn.nhathuoclongchau.com.vn/unsafe/1920x0/filters:quality(90)/https://s3-sgn09.fptcloud.com/cms-prod/kremil_Desktop_805x246_7708f4d4f8.png"
          class="d-block w-100"
          alt="Banner 2"
        />
      </div>
      <div class="carousel-item">
        <img
          src="https://cdn.nhathuoclongchau.com.vn/unsafe/1920x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/Banner_H1_Web_PC_805x246_12f69f69bb.png"
          class="d-block w-100"
          alt="Banner 3"
        />
      </div>
    </div>
    <button
      class="carousel-control-prev"
      type="button"
      data-bs-target="#bannerCarousel"
      data-bs-slide="prev"
    >
      <span class="carousel-control-prev-icon"></span>
      <span class="visually-hidden">Trước</span>
    </button>
    <button
      class="carousel-control-next"
      type="button"
      data-bs-target="#bannerCarousel"
      data-bs-slide="next"
    >
      <span class="carousel-control-next-icon"></span>
      <span class="visually-hidden">Tiếp</span>
    </button>
    <div class="carousel-indicators">
      <button
        type="button"
        data-bs-target="#bannerCarousel"
        data-bs-slide-to="0"
        class="active"
      ></button>
      <button type="button" data-bs-target="#bannerCarousel" data-bs-slide-to="1"></button>
      <button type="button" data-bs-target="#bannerCarousel" data-bs-slide-to="2"></button>
    </div>
  </div>
</template>

<style scoped>
.carousel-inner img {
  border-radius: 8px;
}
</style>

const multer = require('multer');

const storage = multer.memoryStorage(); // Lưu file vào bộ nhớ dưới dạng buffer

const fileFilter = (req, file, cb) => {
    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/jpg'];
    if (allowedTypes.includes(file.mimetype)) {
        cb(null, true);
    } else {
        cb(new Error('Only images are allowed'), false);
    }
};

const upload = multer({
    storage,
    fileFilter,
    limits: { fileSize: 5 * 1024 * 1024 }, // 5MB
});

module.exports = {
    uploadMultiple: upload.array('images', 10), // Field name là 'images'
};
require("dotenv").config();

const { s3 } = require("../configs/configS3");

const randomString = (numberCharacter) => {
    return `${Math.random().toString(36).substring(2, numberCharacter + 2)}`;
};

const File_Type = ["image/png", "image/jpeg", "image/jpg", "image/gif"];


const uploadFile = async (file) => {
    if (!file) {
        throw new Error("File is required");
    }
    if (!File_Type.includes(file.mimetype)) {
        throw new Error("File type is not supported");
    }

    const filePath = `${randomString(4)}-${Date.now()}-${file.originalname}`;
    const params = {
        Bucket: process.env.BUCKET_NAME,
        Body: file.buffer,
        Key: filePath,
        ContentType: file.mimetype,
    };
    try {
        const data = await s3.upload(params).promise();
        return data.Location;
    } catch (error) {
        console.error("Error uploading file to S3:", error);
        throw error;
    }
}

module.exports = { uploadFile };
